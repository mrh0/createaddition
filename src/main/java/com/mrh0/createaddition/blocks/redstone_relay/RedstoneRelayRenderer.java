package com.mrh0.createaddition.blocks.redstone_relay;

import com.mrh0.createaddition.rendering.WireNodeRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RedstoneRelayRenderer extends WireNodeRenderer<RedstoneRelayTileEntity> {

	public RedstoneRelayRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
}

