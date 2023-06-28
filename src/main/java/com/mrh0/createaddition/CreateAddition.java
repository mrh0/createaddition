package com.mrh0.createaddition;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.CANetwork;
import com.mrh0.createaddition.trains.schedule.CASchedule;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
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
    
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAddition.MODID);
    
    private static final String PROTOCOL = "1";
	public static final SimpleChannel Network = new SimpleChannel(new ResourceLocation(MODID, "main"));

    static {
        REGISTRATE.setTooltipModifierFactory(item ->
            new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }

    @Override
    public void onInitialize() {
        ModsLoadedCallback.EVENT.register(envType -> setup());
        onRegisterEffectEvent();

        CommandRegistrationCallback.EVENT.register(CCApiCommand::register);

        ModLoadingContext.registerConfig(MODID, ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FabricLoader.getInstance().getConfigDir().resolve("createaddition-common.toml"));
        
        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("ae2");
        
        new ModGroup("main");
        CABlocks.register();
        CATileEntities.register();
        CAItems.register();
        CAFluids.register();
        CARecipes.register();
        CASchedule.register();
        REGISTRATE.register();

        //  Setup events
        GameEvents.initCommon();

        CANetwork.initServer();

        System.out.println("Create Crafts & Additions Initialized!");
    }

    private void setup() {
    	CAPotatoCannonProjectiles.register();
    	BlockStressValues.registerProvider(MODID, AllConfigs.server().kinetics.stressValues);
    	BoilerHeaters.registerHeater(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
    		BlazeBurnerBlock.HeatLevel value = state.getValue(LiquidBlazeBurnerBlock.HEAT_LEVEL);
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
    }
    
    public static void onRegisterEffectEvent() {
    	CAEffects.register();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
