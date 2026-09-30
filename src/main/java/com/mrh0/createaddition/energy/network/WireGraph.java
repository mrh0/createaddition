package com.mrh0.createaddition.energy.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

/**
 * Every wire node in a dimension and the wires between them, saved with the level.
 *
 * Energy networks are the connected parts of this graph, so energy moves between loaded
 * connectors however many unloaded chunks the wires cross, and no chunk is ever loaded to
 * find out. The world stays the source of truth: block entities re-register whenever they
 * load, what the graph remembers about a chunk is checked when that chunk loads, and
 * anything the graph isn't sure about is checked by WireGraphVerifier.
 */
public class WireGraph extends SavedData {
	private static final String DATA_NAME = CreateAddition.MODID + "_wires";
	private static final int DATA_VERSION = 1;
	private static final Map<ServerLevel, WireGraph> GRAPHS = new WeakHashMap<>();

	private final ServerLevel level;
	private final WireGraphVerifier verifier;

	private final Long2ObjectOpenHashMap<WireVertex> vertices = new Long2ObjectOpenHashMap<>();
	// Position -> vertices that have a wire to that position.
	private final Long2ObjectOpenHashMap<LongOpenHashSet> inbound = new Long2ObjectOpenHashMap<>();
	// Chunk -> positions the graph expects a wire node at, checked when that chunk loads.
	private final Long2ObjectOpenHashMap<LongOpenHashSet> watched = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectOpenHashMap<IWireNode> loadedNodes = new Long2ObjectOpenHashMap<>();
	private final LongOpenHashSet bridges = new LongOpenHashSet();
	// Positions the world has shown to have no wire node, this session.
	private final LongOpenHashSet missing = new LongOpenHashSet();

	private final Map<PortKey, EnergyNetwork> networkByPort = new HashMap<>();
	private final Set<EnergyNetwork> networks = new LinkedHashSet<>();
	private final Set<PortKey> dirtyPorts = new HashSet<>();
	private final Set<WireEnd> danglingCandidates = new LinkedHashSet<>();

	private boolean closed;

	private record WireEnd(long pos, WireSlot slot) {}

	private WireGraph(ServerLevel level) {
		this.level = level;
		this.verifier = new WireGraphVerifier(this, level);
	}

	@Nullable
	public static WireGraph get(@Nullable Level level) {
		if (!(level instanceof ServerLevel serverLevel)) return null;
		WireGraph graph = GRAPHS.get(serverLevel);
		if (graph != null) return graph;
		// Wrapped and simulated levels aren't the real dimension; keep them out of its graph.
		if (serverLevel.getServer().getLevel(serverLevel.dimension()) != serverLevel) return null;
		graph = serverLevel.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
				() -> new WireGraph(serverLevel),
				(tag, registries) -> load(serverLevel, tag)), DATA_NAME);
		GRAPHS.put(serverLevel, graph);
		return graph;
	}

	public static void tick(Level level) {
		WireGraph graph = get(level);
		if (graph != null) graph.tick();
	}

	public static void nodeLoaded(@Nullable Level level, IWireNode node) {
		WireGraph graph = get(level);
		if (graph != null) graph.onNodeLoaded(node);
	}

	public static void nodeUnloaded(@Nullable Level level, IWireNode node) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		WireGraph graph = GRAPHS.get(serverLevel);
		if (graph != null) graph.forgetLoaded(node);
	}

	// A loaded wire node's wires or redstone state changed.
	public static void nodeChanged(@Nullable Level level, IWireNode node) {
		WireGraph graph = get(level);
		if (graph != null) graph.sync(node);
	}

	// A wire node was removed from the world (broken, or picked up by a contraption).
	public static void nodeRemoved(@Nullable Level level, IWireNode node) {
		WireGraph graph = get(level);
		if (graph == null) return;
		graph.forgetLoaded(node);
		graph.markMissing(node.getPos().asLong());
	}

	public static void chunkLoaded(ServerLevel level, LevelChunk chunk) {
		WireGraph graph = get(level);
		if (graph != null) graph.onChunkLoaded(chunk);
	}

	public static void levelUnloaded(ServerLevel level) {
		WireGraph graph = GRAPHS.remove(level);
		if (graph != null) graph.closed = true;
	}

	@Nullable
	public static EnergyNetwork getNetwork(@Nullable Level level, IWireNode node, int port) {
		WireGraph graph = get(level);
		return graph == null ? null : graph.networkOf(node, port);
	}

	public static int getBridgeThroughput(@Nullable Level level, BlockPos pos) {
		WireVertex bridge = getVertex(level, pos);
		return bridge == null ? 0 : bridge.throughput;
	}

	public static int getBridgeDemand(@Nullable Level level, BlockPos pos) {
		WireVertex bridge = getVertex(level, pos);
		return bridge == null ? 0 : bridge.demand;
	}

	@Nullable
	private static WireVertex getVertex(@Nullable Level level, BlockPos pos) {
		WireGraph graph = get(level);
		return graph == null ? null : graph.vertices.get(pos.asLong());
	}

	// The wire node at a position, if its chunk is loaded. Never loads the chunk.
	@Nullable
	public static IWireNode findLoadedNode(ServerLevel level, BlockPos pos) {
		WireGraph graph = get(level);
		return graph != null ? graph.loadedNode(pos.asLong()) : loadedNodeInChunk(level, pos);
	}

	private void onNodeLoaded(IWireNode node) {
		loadedNodes.put(node.getPos().asLong(), node);
		sync(node);
	}

	private void forgetLoaded(IWireNode node) {
		long pos = node.getPos().asLong();
		if (loadedNodes.get(pos) == node) loadedNodes.remove(pos);
	}

	void sync(IWireNode node) {
		BlockState state = node instanceof BlockEntity be ? be.getBlockState() : null;
		markPresent(WireVertex.of(node, state));
	}

	// The world has this wire node
	void markPresent(WireVertex fresh) {
		fresh.verified = true;
		missing.remove(fresh.pos);
		WireVertex old = vertices.get(fresh.pos);
		if (old != null && old.sameWiring(fresh)) {
			old.verified = true;
			if (old.powered != fresh.powered) {
				old.powered = fresh.powered;
				setDirty();
			}
		} else {
			replaceVertex(fresh.pos, old, fresh);
		}
		recheckInbound(fresh.pos);
	}

	// The world has no wire node at this position
	void markMissing(long pos) {
		missing.add(pos);
		WireVertex old = vertices.get(pos);
		if (old != null) replaceVertex(pos, old, null);
		recheckInbound(pos);
	}

	private void onChunkLoaded(LevelChunk chunk) {
		LongOpenHashSet expected = watched.get(chunk.getPos().toLong());
		if (expected == null) return;
		Map<BlockPos, BlockEntity> blockEntities = chunk.getBlockEntities();
		for (long pos : expected.toLongArray()) {
			BlockPos blockPos = BlockPos.of(pos);
			BlockEntity be = blockEntities.get(blockPos);
			// Still there: it registers itself when it loads.
			if (be instanceof IWireNode && !be.isRemoved()) continue;
			CompoundTag pending = chunk.getBlockEntityNbt(blockPos);
			if (pending != null && WireNodeKind.fromBlockEntityId(pending.getString("id")) != null) continue;
			// The graph expected a wire node here and the world has none: drop it, which rebuilds its network.
			markMissing(pos);
		}
	}

	private void replaceVertex(long pos, @Nullable WireVertex old, @Nullable WireVertex fresh) {
		List<WireSlot> cut = new ArrayList<>();
		if (old != null) {
			for (WireSlot slot : old.slots.values()) {
				removeInbound(slot.otherPos(), pos);
				markPartnerDirty(slot);
				if (fresh == null || !slot.equals(fresh.slots.get(slot.index()))) cut.add(slot);
			}
			markPortsDirty(old);
			vertices.remove(pos);
			bridges.remove(pos);
		}
		if (fresh != null) {
			if (old != null) {
				fresh.demand = old.demand;
				fresh.throughput = old.throughput;
			}
			vertices.put(pos, fresh);
			if (fresh.kind.isBridge()) bridges.add(pos);
			for (WireSlot slot : fresh.slots.values()) {
				addInbound(slot.otherPos(), pos);
				markPartnerDirty(slot);
			}
			markPortsDirty(fresh);
			for (WireSlot slot : fresh.slots.values()) checkWireEnd(fresh, slot);
		}
		// The far ends of wires this node no longer has are now one-sided.
		for (WireSlot slot : cut) {
			WireVertex partner = vertices.get(slot.otherPos());
			if (partner == null || !partner.pointsTo(slot.otherIndex(), pos, slot.index())) continue;
			checkWireEnd(partner, partner.slots.get(slot.otherIndex()));
		}
		updateWatch(pos);
		setDirty();
	}

	// Called whenever the two ends of a wire may disagree.
	private void checkWireEnd(WireVertex vertex, WireSlot slot) {
		WireVertex partner = vertices.get(slot.otherPos());
		if (partner != null && partner.pointsTo(slot.otherIndex(), vertex.pos, slot.index())) return;
		if (isDangling(vertex, slot)) danglingCandidates.add(new WireEnd(vertex.pos, slot));
		else verifier.request(slot.otherPos());
	}

	// Only true when the world itself shows the other end is gone or wired elsewhere.
	private boolean isDangling(WireVertex vertex, WireSlot slot) {
		WireVertex partner = vertices.get(slot.otherPos());
		if (partner == null) return missing.contains(slot.otherPos());
		return partner.verified && !partner.pointsTo(slot.otherIndex(), vertex.pos, slot.index());
	}

	private void recheckInbound(long pos) {
		LongOpenHashSet sources = inbound.get(pos);
		if (sources == null) return;
		for (long source : sources.toLongArray()) {
			WireVertex vertex = vertices.get(source);
			if (vertex == null) continue;
			for (WireSlot slot : vertex.slots.values())
				if (slot.otherPos() == pos) checkWireEnd(vertex, slot);
		}
	}

	private void pruneDanglingWires() {
		if (danglingCandidates.isEmpty()) return;
		List<WireEnd> candidates = new ArrayList<>(danglingCandidates);
		danglingCandidates.clear();
		for (WireEnd end : candidates) {
			WireVertex vertex = vertices.get(end.pos());
			WireSlot slot = end.slot();
			if (vertex == null || !slot.equals(vertex.slots.get(slot.index())) || !isDangling(vertex, slot)) continue;
			IWireNode node = loadedNode(end.pos());
			if (node != null) {
				LocalNode local = node.getLocalNode(slot.index());
				if (local != null && local.getPos().asLong() == slot.otherPos() && local.getOtherIndex() == slot.otherIndex())
					node.removeNode(slot.index());
				else
					sync(node);
				continue;
			}
			// Not loaded: forget our copy of the wire; the block entity lets go of it when it next loads.
			// Until then our copy no longer matches the world, so it can't be used to judge other wires.
			vertex.slots.remove(slot.index());
			vertex.verified = false;
			removeInbound(slot.otherPos(), vertex.pos);
			dirtyPorts.add(vertex.port(vertex.kind.portOf(slot.index())));
			setDirty();
		}
	}

	// Network
	private EnergyNetwork networkOf(IWireNode node, int port) {
		long pos = node.getPos().asLong();
		if (!vertices.containsKey(pos)) onNodeLoaded(node);
		PortKey key = new PortKey(pos, port);
		EnergyNetwork network = networkByPort.get(key);
		if (network == null || !network.isValid() || dirtyPorts.contains(key)) {
			rebuildNetworks();
			network = networkByPort.get(key);
		}
		if (network == null) {
			// A port this node's kind doesn't have; still hand out something usable.
			network = new EnergyNetwork();
			network.invalidate();
		}
		return network;
	}

	private void rebuildNetworks() {
		if (dirtyPorts.isEmpty()) return;
		Set<PortKey> seeds = new HashSet<>(dirtyPorts);
		dirtyPorts.clear();
		Set<EnergyNetwork> previous = Collections.newSetFromMap(new IdentityHashMap<>());
		for (PortKey port : seeds) {
			EnergyNetwork network = networkByPort.get(port);
			if (network != null) previous.add(network);
		}
		// Any change can split a network, so walk every touched network in full.
		for (EnergyNetwork network : previous) seeds.addAll(network.members);

		Set<PortKey> visited = new HashSet<>();
		List<Set<PortKey>> components = new ArrayList<>();
		for (PortKey seed : seeds)
			if (!visited.contains(seed) && exists(seed)) components.add(walk(seed, visited));
		// A new wire can join a network nobody touched; that one changes too.
		for (Set<PortKey> component : components)
			for (PortKey port : component) {
				EnergyNetwork network = networkByPort.get(port);
				if (network != null) previous.add(network);
			}
		for (PortKey port : seeds)
			if (!visited.contains(port)) networkByPort.remove(port);

		Set<EnergyNetwork> unchanged = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Set<PortKey> component : components) {
			EnergyNetwork current = networkByPort.get(component.iterator().next());
			// Same members as before: keep it, so nothing using it has to look it up again.
			if (current != null && current.members.equals(component)) {
				unchanged.add(current);
				continue;
			}
			EnergyNetwork network = new EnergyNetwork();
			network.members = component;
			for (PortKey port : component) networkByPort.put(port, network);
			networks.add(network);
		}
		for (EnergyNetwork old : previous) {
			if (unchanged.contains(old)) continue;
			// Stored energy goes wherever most of the old network ended up.
			if (old.hasStoredEnergy()) {
				EnergyNetwork heir = largestShare(old.members);
				if (heir != null && heir != old) heir.absorb(old);
			}
			old.invalidate();
			networks.remove(old);
		}
	}

	private boolean exists(PortKey port) {
		WireVertex vertex = vertices.get(port.pos());
		return vertex != null && port.port() >= 0 && port.port() < vertex.kind.portCount();
	}

	private Set<PortKey> walk(PortKey start, Set<PortKey> visited) {
		Set<PortKey> component = new HashSet<>();
		ArrayDeque<PortKey> queue = new ArrayDeque<>();
		visited.add(start);
		queue.add(start);
		while (!queue.isEmpty()) {
			PortKey port = queue.poll();
			component.add(port);
			WireVertex vertex = vertices.get(port.pos());
			for (WireSlot slot : vertex.slots.values()) {
				if (vertex.kind.portOf(slot.index()) != port.port()) continue;
				WireVertex other = vertices.get(slot.otherPos());
				// A wire only counts once both of its ends agree it exists.
				if (other == null || !other.pointsTo(slot.otherIndex(), vertex.pos, slot.index())) continue;
				PortKey next = other.port(other.kind.portOf(slot.otherIndex()));
				if (visited.add(next)) queue.add(next);
			}
		}
		return component;
	}

	@Nullable
	private EnergyNetwork largestShare(Set<PortKey> ports) {
		Map<EnergyNetwork, Integer> shares = new IdentityHashMap<>();
		EnergyNetwork best = null;
		int bestShare = 0;
		for (PortKey port : ports) {
			EnergyNetwork network = networkByPort.get(port);
			if (network == null) continue;
			int share = shares.merge(network, 1, Integer::sum);
			if (share > bestShare) {
				best = network;
				bestShare = share;
			}
		}
		return best;
	}

	private void tick() {
		if (closed) return;
		verifier.tick();
		pruneDanglingWires();
		rebuildNetworks();
		int id = 0;
		boolean stored = false;
		for (EnergyNetwork network : networks) {
			network.tick(id++);
			stored |= network.hasStoredEnergy();
		}
		tickBridges();
		// Stored energy is saved with the graph.
		if (stored) setDirty();
	}

	// Relays run here rather than in their block entity, so they keep working while unloaded.
	private void tickBridges() {
		LongIterator it = bridges.iterator();
		while (it.hasNext()) {
			WireVertex bridge = vertices.get(it.nextLong());
			if (bridge == null) continue;
			bridge.throughput = 0;
			if (!bridge.powered) continue;
			EnergyNetwork in = networkByPort.get(bridge.port(0));
			EnergyNetwork out = networkByPort.get(bridge.port(1));
			if (in == null || out == null) continue;
			bridge.throughput = out.push(in.pull(bridge.demand));
			bridge.demand = in.demand(out.getDemand());
		}
	}

	boolean isClosed() {
		return closed;
	}

	boolean isVerified(long pos) {
		WireVertex vertex = vertices.get(pos);
		return (vertex != null && vertex.verified) || missing.contains(pos);
	}

	boolean isLoaded(long pos) {
		return loadedNodes.containsKey(pos) || level.getChunkSource().getChunkNow(
				SectionPos.blockToSectionCoord(BlockPos.getX(pos)), SectionPos.blockToSectionCoord(BlockPos.getZ(pos))) != null;
	}

	@Nullable
	IWireNode loadedNode(long pos) {
		IWireNode node = loadedNodes.get(pos);
		if (node instanceof BlockEntity be && !be.isRemoved()) return node;
		return loadedNodeInChunk(level, BlockPos.of(pos));
	}

	@Nullable
	private static IWireNode loadedNodeInChunk(ServerLevel level, BlockPos pos) {
		LevelChunk chunk = level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
		if (chunk == null) return null;
		BlockEntity be = chunk.getBlockEntity(pos);
		return be instanceof IWireNode node && !be.isRemoved() ? node : null;
	}

	private void markPortsDirty(WireVertex vertex) {
		for (int port = 0; port < vertex.kind.portCount(); port++)
			dirtyPorts.add(vertex.port(port));
	}

	private void markPartnerDirty(WireSlot slot) {
		WireVertex partner = vertices.get(slot.otherPos());
		if (partner != null) dirtyPorts.add(partner.port(partner.kind.portOf(slot.otherIndex())));
	}

	private void addInbound(long target, long source) {
		positionsAt(inbound, target).add(source);
		updateWatch(target);
	}

	private void removeInbound(long target, long source) {
		LongOpenHashSet sources = inbound.get(target);
		if (sources == null) return;
		sources.remove(source);
		if (sources.isEmpty()) inbound.remove(target);
		updateWatch(target);
	}

	private void updateWatch(long pos) {
		long chunk = ChunkPos.asLong(SectionPos.blockToSectionCoord(BlockPos.getX(pos)), SectionPos.blockToSectionCoord(BlockPos.getZ(pos)));
		if (vertices.containsKey(pos) || inbound.containsKey(pos)) {
			positionsAt(watched, chunk).add(pos);
			return;
		}
		LongOpenHashSet positions = watched.get(chunk);
		if (positions != null && positions.remove(pos) && positions.isEmpty()) watched.remove(chunk);
	}

	private static LongOpenHashSet positionsAt(Long2ObjectOpenHashMap<LongOpenHashSet> map, long key) {
		LongOpenHashSet positions = map.get(key);
		if (positions == null) {
			positions = new LongOpenHashSet();
			map.put(key, positions);
		}
		return positions;
	}

	// Commands
	public Component describe() {
		int verified = 0;
		for (WireVertex vertex : vertices.values())
			if (vertex.verified) verified++;
		return Component.literal(vertices.size() + " wire nodes (" + loadedNodes.size() + " loaded, " + verified
				+ " checked against the world), " + networks.size() + " networks, "
				+ verifier.pendingChunkReads() + " chunks waiting to be read from disk");
	}

	// Checks every node the graph only knows from its save against the world
	public int verifyAll() {
		int count = 0;
		for (WireVertex vertex : vertices.values()) {
			if (vertex.verified) continue;
			verifier.request(vertex.pos);
			count++;
		}
		return count;
	}

	// Starts over from the loaded wire nodes; the rest is read back from disk
	public void rebuild() {
		ListTag buffers = writeBuffers();
		List<IWireNode> loaded = new ArrayList<>(loadedNodes.values());
		for (EnergyNetwork network : networks) network.invalidate();
		vertices.clear();
		inbound.clear();
		watched.clear();
		loadedNodes.clear();
		bridges.clear();
		missing.clear();
		networkByPort.clear();
		networks.clear();
		dirtyPorts.clear();
		danglingCandidates.clear();
		verifier.reset();
		for (IWireNode node : loaded)
			if (node instanceof BlockEntity be && !be.isRemoved()) onNodeLoaded(node);
		rebuildNetworks();
		restoreBuffers(buffers);
		setDirty();
	}

	// Save
	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putInt("Version", DATA_VERSION);
		ListTag list = new ListTag();
		for (WireVertex vertex : vertices.values()) list.add(vertex.write());
		tag.put("Vertices", list);
		tag.put("Buffers", writeBuffers());
		return tag;
	}

	private static WireGraph load(ServerLevel level, CompoundTag tag) {
		WireGraph graph = new WireGraph(level);
		for (Tag t : tag.getList("Vertices", Tag.TAG_COMPOUND)) {
			WireVertex vertex = WireVertex.read((CompoundTag) t);
			if (vertex != null) graph.addSavedVertex(vertex);
		}
		graph.rebuildNetworks();
		graph.restoreBuffers(tag.getList("Buffers", Tag.TAG_COMPOUND));
		// Carry on where discovery left off: wires to nodes the graph has never seen.
		for (WireVertex vertex : graph.vertices.values())
			for (WireSlot slot : vertex.slots.values())
				if (!graph.vertices.containsKey(slot.otherPos())) graph.verifier.request(slot.otherPos());
		return graph;
	}

	// Trusted until its chunk loads or something disagrees with it
	private void addSavedVertex(WireVertex vertex) {
		vertices.put(vertex.pos, vertex);
		if (vertex.kind.isBridge()) bridges.add(vertex.pos);
		for (WireSlot slot : vertex.slots.values()) addInbound(slot.otherPos(), vertex.pos);
		updateWatch(vertex.pos);
		markPortsDirty(vertex);
	}

	private ListTag writeBuffers() {
		ListTag buffers = new ListTag();
		for (EnergyNetwork network : networks) {
			if (!network.hasStoredEnergy() || network.members.isEmpty()) continue;
			PortKey anchor = network.members.iterator().next();
			CompoundTag buffer = new CompoundTag();
			buffer.putLong("Pos", anchor.pos());
			buffer.putInt("Port", anchor.port());
			buffer.putInt("In", network.getStoredIn());
			buffer.putInt("Out", network.getStoredOut());
			buffers.add(buffer);
		}
		return buffers;
	}

	private void restoreBuffers(ListTag buffers) {
		for (Tag t : buffers) {
			CompoundTag buffer = (CompoundTag) t;
			EnergyNetwork network = networkByPort.get(new PortKey(buffer.getLong("Pos"), buffer.getInt("Port")));
			if (network != null) network.restore(buffer.getInt("In"), buffer.getInt("Out"));
		}
	}
}
