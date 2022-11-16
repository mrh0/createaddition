package com.mrh0.createaddition;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurner;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.CANetwork;
import com.simibubi.create.content.contraptions.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateAddition implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "createaddition";
    
    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    public static boolean AE2_ACTIVE = false;
    
    private static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(CreateAddition.MODID);

    @Override
    public void onInitialize() {
        CARecipes.register();

        ModLoadingContext.registerConfig(CreateAddition.MODID, ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FabricLoader.getInstance().getConfigDir().resolve("createaddition-common.toml"));
        
        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("ae2");

        BoilerHeaters.registerHeater(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(LiquidBlazeBurner.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) {
                return -1;
            }
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) {
                return 2;
            }
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
                return 1;
            }
            return 0;
        });
        

        CABlocks.register();
        CATileEntities.register();
        CAItems.register();
        CAFluids.register();
        CAEffects.register();
        //CAEntities.register();
        CAPotatoCannonProjectiles.register();
        CommandRegistrationCallback.EVENT.register(CCApiCommand::register);
        registrate.get().register();
        GameEvents.initCommon();
        postInit();
    }

    public void postInit() {
    	int i = 0;
        CANetwork.initServer();
        
        
        /*
        FurnaceEngineInteractions.registerHandler(CABlocks.FURNACE_BURNER.get(), FurnaceEngineInteractions.InteractionHandler.of(
       		 s -> s.getBlock() instanceof FurnaceBurner && s.hasProperty(FurnaceBurner.LIT) ? 
       		 (s.getValue(FurnaceBurner.LIT) ? HeatSource.ACTIVE : HeatSource.VALID) : HeatSource.EMPTY, s -> (float)(double)Config.FURNACE_BURNER_ENGINE_SPEED.get()));
        
        FurnaceEngineInteractions.registerHandler(CABlocks.CRUDE_BURNER.get(), FurnaceEngineInteractions.InteractionHandler.of(
          		 s -> s.getBlock() instanceof CrudeBurner && s.hasProperty(CrudeBurner.LIT) ? 
          	       		 (s.getValue(CrudeBurner.LIT) ? HeatSource.ACTIVE : HeatSource.VALID) : HeatSource.EMPTY, s -> (float)(double)Config.CRUDE_BURNER_ENGINE_SPEED.get()));
        */
    	System.out.println("Create Crafts & Addition Initialized!");
    }

    public static CreateRegistrate registrate() {
		return registrate.get();
	}
}
