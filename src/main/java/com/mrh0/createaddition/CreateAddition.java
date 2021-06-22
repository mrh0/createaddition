package com.mrh0.createaddition;

import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CAPonder;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;

@Mod(CreateAddition.MODID)
public class CreateAddition {
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "createaddition";
    
    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    
    private static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(CreateAddition.MODID);
    
    private static final String PROTOCOL = "1";
	public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    public CreateAddition() {
    	
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
        
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, CARecipes::register);
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeType.class, CARecipes::register);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createaddition-common.toml"));
        
        IE_ACTIVE = ModList.get().isLoaded("immersiveengineering");
        CC_ACTIVE = ModList.get().isLoaded("computercraft");
        
        new ModGroup("main");
        
        CABlocks.register();
        CATileEntities.register();
        CAItems.register();
        CAFluids.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
    	
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        CAPonder.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {
    	
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    	
    }
    
    public void postInit(FMLLoadCompleteEvent evt) {
    	int i = 0;
        Network.registerMessage(i++, ObservePacket.class, ObservePacket::encode, ObservePacket::decode, ObservePacket::handle);
        Network.registerMessage(i++, EnergyNetworkPacket.class, EnergyNetworkPacket::encode, EnergyNetworkPacket::decode, EnergyNetworkPacket::handle);
    	System.out.println("Create Crafts & Addition Initialized!");
    }
    
    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
    	CommandDispatcher<CommandSource> dispather = event.getDispatcher();
    	CCApiCommand.register(dispather);
    }
    
    public static CreateRegistrate registrate() {
		return registrate.get();
	}
}
