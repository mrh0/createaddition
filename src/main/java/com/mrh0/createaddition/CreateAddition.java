package com.mrh0.createaddition;

import com.mojang.brigadier.CommandDispatcher;
import com.mrh0.createaddition.blocks.crude_burner.CrudeBurner;
import com.mrh0.createaddition.blocks.furnace_burner.FurnaceBurner;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.content.contraptions.components.flywheel.engine.FurnaceEngineInteractions;
import com.simibubi.create.content.contraptions.components.flywheel.engine.FurnaceEngineInteractions.HeatSource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateAddition implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "createaddition";
    
    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    public static boolean AE2_ACTIVE = false;
    
    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(CreateAddition.MODID);
    
    private static final String PROTOCOL = "1";

    @Override
    public void onInitialize() {
        
        CARecipes.register();
        
        Config.loadConfig(Config.COMMON_CONFIG, FabricLoader.getInstance().getConfigDir().resolve("createaddition-common.toml"));
        
        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("appliedenergistics2");
        
        ModGroup.init();
        
        CABlocks.register();
        CATileEntities.register();
        CAItems.register();
        CAFluids.register();
        CAEffects.register();
        //CAEntities.register();
        CommandRegistrationCallback.EVENT.register(this::onRegisterCommandEvent);
        CAPotatoCannonProjectiles.register();
        postInit();
        GameEvents.initCommon();
        registrate.get().register();
        CATileEntities.registerStorages();
    }

    
    public void postInit() {
        FurnaceEngineInteractions.registerHandler(CABlocks.FURNACE_BURNER.get(), FurnaceEngineInteractions.InteractionHandler.of(
       		 s -> s.getBlock() instanceof FurnaceBurner && s.hasProperty(FurnaceBurner.LIT) ? 
       		 (s.getValue(FurnaceBurner.LIT) ? HeatSource.ACTIVE : HeatSource.VALID) : HeatSource.EMPTY, s -> (float)(double)Config.FURNACE_BURNER_ENGINE_SPEED.get()));
        
        FurnaceEngineInteractions.registerHandler(CABlocks.CRUDE_BURNER.get(), FurnaceEngineInteractions.InteractionHandler.of(
          		 s -> s.getBlock() instanceof CrudeBurner && s.hasProperty(CrudeBurner.LIT) ? 
          	       		 (s.getValue(CrudeBurner.LIT) ? HeatSource.ACTIVE : HeatSource.VALID) : HeatSource.EMPTY, s -> (float)(double)Config.CRUDE_BURNER_ENGINE_SPEED.get()));
        
    	System.out.println("Create Crafts & Addition Initialized!");
    }

    public void onRegisterCommandEvent(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
    	CCApiCommand.register(dispatcher);
    }
    
    public static CreateRegistrate registrate() {
		return registrate.get();
	}
}
