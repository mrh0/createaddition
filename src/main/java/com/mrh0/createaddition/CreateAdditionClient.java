package com.mrh0.createaddition;

import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItemProperties;
import com.mrh0.createaddition.index.CAPonder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class CreateAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GameEvents.initClient();
        CAPonder.register();
        //CAEntities.registerRenderers();
        CAItemProperties.register();

        RenderType cutout = RenderType.cutoutMipped();

        BlockRenderLayerMap.INSTANCE.putBlock(CABlocks.TESLA_COIL.get(), cutout);
    }
}
