package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.rendering.WireNodeRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class ConnectorRenderer extends WireNodeRenderer<ConnectorTileEntity> {

	public ConnectorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
}

