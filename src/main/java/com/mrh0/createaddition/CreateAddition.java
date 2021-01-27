package com.mrh0.createaddition;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateAddition.MODID)
public class CreateAddition {
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "createaddition";
    
    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(CreateAddition.MODID);

    public CreateAddition() {
    	
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, CARecipes::register);
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeType.class, CARecipes::register);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createaddition-common.toml"));
        
        new ModGroup("main");
        
        CABlocks.register();
        CATileEntities.register();
        CAItems.register();
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        
    }

    private void processIMC(final InterModProcessEvent event) {
    	
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }
    
    public static CreateRegistrate registrate() {
		return registrate.get();
	}
}
