package com.mrh0.createaddition.blocks.connector.base;

import com.mrh0.createaddition.rendering.WireNodeRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;


public class ConnectorRenderer extends WireNodeRenderer<AbstractConnectorBlockEntity> {

	public ConnectorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
}

