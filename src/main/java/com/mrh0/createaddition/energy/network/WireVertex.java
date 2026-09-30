package com.mrh0.createaddition.energy.network;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;
import com.mrh0.createaddition.energy.WireType;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

final class WireVertex {
	// Old block entities stored their nodes as node0..node7, see IWireNode#convertOldNbt.
	private static final int LEGACY_NODE_LIMIT = 8;

	final long pos;
	final WireNodeKind kind;
	final Int2ObjectMap<WireSlot> slots = new Int2ObjectArrayMap<>();
	// Last known redstone state of a bridge, kept while its chunk is unloaded.
	boolean powered;

	// Whether this copy was read from the world this session, rather than from the graphs own save.
	boolean verified;
	int demand;
	int throughput;

	WireVertex(long pos, WireNodeKind kind) {
		this.pos = pos;
		this.kind = kind;
	}

	static WireVertex of(IWireNode node, @Nullable BlockState state) {
		WireVertex vertex = new WireVertex(node.getPos().asLong(), node.getWireNodeKind());
		for (int i = 0; i < node.getNodeCount(); i++) {
			LocalNode local = node.getLocalNode(i);
			if (local == null || local.getType() == null) continue;
			vertex.slots.put(i, new WireSlot(i, local.getPos().asLong(), local.getOtherIndex(), local.getType()));
		}
		vertex.powered = vertex.kind.isPowered(state);
		return vertex;
	}

	static WireVertex fromBlockEntityTag(BlockPos pos, WireNodeKind kind, CompoundTag tag) {
		WireVertex vertex = new WireVertex(pos.asLong(), kind);
		for (Tag t : tag.getList(LocalNode.NODES, Tag.TAG_COMPOUND)) {
			CompoundTag node = (CompoundTag) t;
			WireType type = WireType.fromIndex(node.getInt(LocalNode.TYPE));
			if (type == null) continue;
			int index = node.getInt(LocalNode.ID);
			BlockPos other = pos.offset(node.getInt(LocalNode.X), node.getInt(LocalNode.Y), node.getInt(LocalNode.Z));
			vertex.slots.put(index, new WireSlot(index, other.asLong(), node.getInt(LocalNode.OTHER), type));
		}
		for (int i = 0; i < LEGACY_NODE_LIMIT; i++) {
			if (!tag.contains("node" + i) || tag.getInt("node" + i) == -1) continue;
			WireType type = WireType.fromIndex(tag.getInt("type" + i));
			if (type == null) continue;
			BlockPos other = new BlockPos(tag.getInt("x" + i), tag.getInt("y" + i), tag.getInt("z" + i));
			vertex.slots.put(i, new WireSlot(i, other.asLong(), tag.getInt("node" + i), type));
		}
		return vertex;
	}

	boolean pointsTo(int index, long otherPos, int otherIndex) {
		WireSlot slot = slots.get(index);
		return slot != null && slot.otherPos() == otherPos && slot.otherIndex() == otherIndex;
	}

	boolean sameWiring(WireVertex other) {
		return kind == other.kind && slots.equals(other.slots);
	}

	PortKey port(int port) {
		return new PortKey(pos, port);
	}

	CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		tag.putLong("Pos", pos);
		tag.putString("Kind", kind.getSerializedName());
		if (kind.isBridge()) tag.putBoolean("Powered", powered);
		ListTag list = new ListTag();
		for (WireSlot slot : slots.values()) {
			CompoundTag slotTag = new CompoundTag();
			slotTag.putInt("Index", slot.index());
			slotTag.putLong("Other", slot.otherPos());
			slotTag.putInt("OtherIndex", slot.otherIndex());
			slotTag.putInt("Type", slot.type().getIndex());
			list.add(slotTag);
		}
		tag.put("Slots", list);
		return tag;
	}

	@Nullable
	static WireVertex read(CompoundTag tag) {
		WireNodeKind kind = WireNodeKind.byName(tag.getString("Kind"));
		if (kind == null) return null;
		WireVertex vertex = new WireVertex(tag.getLong("Pos"), kind);
		vertex.powered = tag.getBoolean("Powered");
		for (Tag t : tag.getList("Slots", Tag.TAG_COMPOUND)) {
			CompoundTag slotTag = (CompoundTag) t;
			WireType type = WireType.fromIndex(slotTag.getInt("Type"));
			if (type == null) continue;
			int index = slotTag.getInt("Index");
			vertex.slots.put(index, new WireSlot(index, slotTag.getLong("Other"), slotTag.getInt("OtherIndex"), type));
		}
		return vertex;
	}
}
