package com.mrh0.createaddition.energy.network;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mrh0.createaddition.index.CABlockEntities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public enum WireNodeKind {
	CONNECTOR,
	// Nodes 0-3 are the input port, 4-7 the output port. Moves energy from input to output while powered.
	RELAY;

	private static Map<ResourceLocation, WireNodeKind> byBlockEntityId;

	public int portOf(int node) {
		return this == RELAY && node >= 4 ? 1 : 0;
	}

	public int portCount() {
		return this == RELAY ? 2 : 1;
	}

	public boolean isBridge() {
		return this == RELAY;
	}

	public boolean isPowered(@Nullable BlockState state) {
		return isBridge() && state != null && state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED);
	}

	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	@Nullable
	public static WireNodeKind byName(String name) {
		for (WireNodeKind kind : values())
			if (kind.getSerializedName().equals(name)) return kind;
		return null;
	}

	@Nullable
	public static WireNodeKind fromBlockEntityId(String id) {
		if (byBlockEntityId == null) {
			Map<ResourceLocation, WireNodeKind> map = new HashMap<>();
			map.put(BlockEntityType.getKey(CABlockEntities.SMALL_CONNECTOR.get()), CONNECTOR);
			map.put(BlockEntityType.getKey(CABlockEntities.SMALL_LIGHT_CONNECTOR.get()), CONNECTOR);
			map.put(BlockEntityType.getKey(CABlockEntities.LARGE_CONNECTOR.get()), CONNECTOR);
			map.put(BlockEntityType.getKey(CABlockEntities.REDSTONE_RELAY.get()), RELAY);
			byBlockEntityId = map;
		}
		ResourceLocation key = ResourceLocation.tryParse(id);
		return key == null ? null : byBlockEntityId.get(key);
	}
}
