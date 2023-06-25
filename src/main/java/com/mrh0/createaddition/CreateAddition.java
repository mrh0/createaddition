package com.mrh0.createaddition;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurner;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.mrh0.createaddition.compat.computercraft.ComputerCraftCompat;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.CANetwork;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CreateAddition implements ModInitializer {

    public static final String MODID = "createaddition";
    
    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    public static boolean AE2_ACTIVE = false;
    
    private static final NonNullSupplier<CreateRegistrate> REGISTRATE =
            NonNullSupplier.lazy(() -> CreateRegistrate.create(CreateAddition.MODID));

    @Override
    public void onInitialize() {
        CARecipes.register();

        ModLoadingContext.registerConfig(CreateAddition.MODID, ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FabricLoader.getInstance().getConfigDir().resolve("createaddition-common.toml"));
        
        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("ae2");

        BoilerHeaters.registerHeater(new ResourceLocation(MODID, "liquid_blaze_burner"), (level, pos, state) -> {
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
        REGISTRATE.get().register();
        GameEvents.initCommon();
        postInit();

        if  (CC_ACTIVE) {
            ComputerCraftCompat.registerCompat();
        }
    }

    public void postInit() {
        CANetwork.initServer();
    	System.out.println("Create Crafts & Addition Initialized!");
    }

    public static CreateRegistrate registrate() {
		return REGISTRATE.get();
	}
}
