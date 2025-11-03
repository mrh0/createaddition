package com.mrh0.createaddition;

import com.mrh0.createaddition.event.ClientEventHandler;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItemProperties;
import com.mrh0.createaddition.index.CAPartials;
import com.mrh0.createaddition.index.CAPonders;
import com.mrh0.createaddition.network.CANetwork;
import com.mrh0.createaddition.ponder.CAPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.renderer.RenderType;

public class CreateAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CANetwork.initClient();
        GameEvents.initClient();
//        CAPonders.register();
        //CAEntities.registerRenderers();
        PonderIndex.addPlugin(new CAPonderPlugin());
        CAPartials.init();
        CAItemProperties.register();

        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::playerRendererEvent);

        RenderType cutout = RenderType.cutoutMipped();

        BlockRenderLayerMap.INSTANCE.putBlocks(cutout, CABlocks.TESLA_COIL.get(), CABlocks.BARBED_WIRE.get(), CABlocks.SMALL_LIGHT_CONNECTOR.get());
    }
}
