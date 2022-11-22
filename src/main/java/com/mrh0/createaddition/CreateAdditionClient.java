package com.mrh0.createaddition;

import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItemProperties;
import com.mrh0.createaddition.index.CAPartials;
import com.mrh0.createaddition.index.CAPonder;
import com.mrh0.createaddition.network.CANetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class CreateAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CANetwork.initClient();
        GameEvents.initClient();
        CAPonder.register();
        //CAEntities.registerRenderers();
        CAPartials.initClass();
        CAItemProperties.register();

        RenderType cutout = RenderType.cutoutMipped();

        BlockRenderLayerMap.INSTANCE.putBlock(CABlocks.TESLA_COIL.get(), cutout);
    }
}
