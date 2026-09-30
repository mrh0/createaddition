package com.mrh0.createaddition.energy.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.sable.SableUtil;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jetbrains.annotations.Nullable;

final class WireGraphVerifier {
	private static final int MAX_READS_IN_FLIGHT = 16;
	private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW(
			Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());

	private final WireGraph graph;
	private final ServerLevel level;
	// Positions in loaded chunks, checked next tick so blocks placed together (contraptions, pastes) are all in place first.
	private LongOpenHashSet queued = new LongOpenHashSet();
	// Chunk -> positions waiting on that chunk's saved data.
	private final Long2ObjectOpenHashMap<LongOpenHashSet> waitingForDisk = new Long2ObjectOpenHashMap<>();
	private final LongArrayFIFOQueue readQueue = new LongArrayFIFOQueue();
	private final LongOpenHashSet readsInFlight = new LongOpenHashSet();
	// Positions the world has no answer for this session: no saved chunk, or data we can't read.
	private final LongOpenHashSet unresolved = new LongOpenHashSet();

	WireGraphVerifier(WireGraph graph, ServerLevel level) {
		this.graph = graph;
		this.level = level;
	}

	void request(long pos) {
		if (unresolved.contains(pos) || graph.isVerified(pos)) return;
		if (graph.isLoaded(pos)) queued.add(pos);
		else readFromDisk(pos);
	}

	void tick() {
		if (!queued.isEmpty()) {
			LongOpenHashSet now = queued;
			queued = new LongOpenHashSet();
			LongIterator it = now.iterator();
			while (it.hasNext()) checkLoaded(it.nextLong());
		}
		startReads();
	}

	int pendingChunkReads() {
		return waitingForDisk.size();
	}

	void reset() {
		queued.clear();
		waitingForDisk.clear();
		readQueue.clear();
		unresolved.clear();
	}

	private void checkLoaded(long pos) {
		if (graph.isVerified(pos)) return;
		IWireNode node = graph.loadedNode(pos);
		if (node != null) graph.sync(node);
		else if (graph.isLoaded(pos)) graph.markMissing(pos);
		else readFromDisk(pos);
	}

	private void readFromDisk(long pos) {
		// Sub-level plots may not be stored like normal chunks; their nodes register when they load.
		if (CreateAddition.SABLE_ACTIVE && SableUtil.isInSubLevel(level, BlockPos.of(pos))) {
			unresolved.add(pos);
			return;
		}
		long chunk = ChunkPos.asLong(SectionPos.blockToSectionCoord(BlockPos.getX(pos)), SectionPos.blockToSectionCoord(BlockPos.getZ(pos)));
		LongOpenHashSet positions = waitingForDisk.get(chunk);
		if (positions == null) {
			positions = new LongOpenHashSet();
			waitingForDisk.put(chunk, positions);
			if (!readsInFlight.contains(chunk)) readQueue.enqueue(chunk);
		}
		positions.add(pos);
	}

	private void startReads() {
		while (readsInFlight.size() < MAX_READS_IN_FLIGHT && !readQueue.isEmpty()) {
			long chunk = readQueue.dequeueLong();
			if (!waitingForDisk.containsKey(chunk) || !readsInFlight.add(chunk)) continue;
			// Reads the chunk as saved (or queued for saving) without loading it or adding a ticket.
			level.getChunkSource().chunkMap.read(new ChunkPos(chunk))
					.whenCompleteAsync((tag, error) -> onRead(chunk, tag, error), level.getServer());
		}
	}

	private void onRead(long chunk, @Nullable Optional<CompoundTag> tag, @Nullable Throwable error) {
		readsInFlight.remove(chunk);
		LongOpenHashSet positions = waitingForDisk.remove(chunk);
		if (graph.isClosed() || positions == null) return;
		ChunkPos chunkPos = new ChunkPos(chunk);
		LongIterator it = positions.iterator();
		if (level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null) {
			// Loaded while we were reading: the live chunk is the better answer.
			while (it.hasNext()) request(it.nextLong());
		} else if (error != null || tag == null || tag.isEmpty()) {
			if (error != null) CreateAddition.LOGGER.warn("Could not read chunk {} to check wire nodes", chunkPos, error);
			unresolved.addAll(positions);
		} else {
			SavedChunk saved = new SavedChunk(tag.get());
			while (it.hasNext()) resolve(it.nextLong(), saved);
		}
		startReads();
	}

	private void resolve(long pos, SavedChunk saved) {
		// A loaded block entity may have answered while the read was in flight.
		if (graph.isVerified(pos)) return;
		if (!saved.isReadable()) {
			unresolved.add(pos);
			return;
		}
		BlockPos blockPos = BlockPos.of(pos);
		CompoundTag blockEntity = saved.blockEntity(blockPos);
		WireNodeKind kind = blockEntity == null ? null : WireNodeKind.fromBlockEntityId(blockEntity.getString("id"));
		if (kind == null) {
			// Wire nodes from other mods can't be read here; anything else means the node is gone.
			if (blockEntity != null && blockEntity.contains(LocalNode.NODES)) unresolved.add(pos);
			else graph.markMissing(pos);
			return;
		}
		// Placed by a contraption but never ticked, so its wires haven't been rotated into place yet.
		if (blockEntity.contains("contraption")) {
			unresolved.add(pos);
			return;
		}
		WireVertex vertex = WireVertex.fromBlockEntityTag(blockPos, kind, blockEntity);
		vertex.powered = kind.isPowered(saved.blockState(blockPos));
		graph.markPresent(vertex);
	}

	private static final class SavedChunk {
		private final CompoundTag tag;
		@Nullable
		private Map<BlockPos, CompoundTag> blockEntities;

		SavedChunk(CompoundTag tag) {
			this.tag = tag;
		}

		boolean isReadable() {
			return !tag.contains("Level", Tag.TAG_COMPOUND);
		}

		@Nullable
		CompoundTag blockEntity(BlockPos pos) {
			if (blockEntities == null) {
				blockEntities = new HashMap<>();
				for (Tag t : tag.getList("block_entities", Tag.TAG_COMPOUND)) {
					CompoundTag blockEntity = (CompoundTag) t;
					blockEntities.put(BlockEntity.getPosFromTag(blockEntity), blockEntity);
				}
			}
			return blockEntities.get(pos);
		}

		@Nullable
		BlockState blockState(BlockPos pos) {
			int sectionY = SectionPos.blockToSectionCoord(pos.getY());
			for (Tag t : tag.getList("sections", Tag.TAG_COMPOUND)) {
				CompoundTag section = (CompoundTag) t;
				if (section.getByte("Y") != sectionY || !section.contains("block_states", Tag.TAG_COMPOUND)) continue;
				return BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, section.getCompound("block_states")).result()
						.map(states -> states.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15))
						.orElse(null);
			}
			return null;
		}
	}
}
