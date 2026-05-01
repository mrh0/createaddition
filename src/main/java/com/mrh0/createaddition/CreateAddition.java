package com.mrh0.createaddition;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.index.CASounds;
import com.mrh0.createaddition.network.*;
import com.mrh0.createaddition.ponder.CAPonderPlugin;
import com.mrh0.createaddition.trains.schedule.CASchedule;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.api.boiler.BoilerHeater;

@Mod(CreateAddition.MODID)
public class CreateAddition {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "createaddition";

    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    public static boolean AE2_ACTIVE = false;
    public static boolean MEK_ACTIVE = false;
    public static boolean SIM_ACTIVE = false;
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAddition.MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private static final ItemLike[] excludedItemsList = new ItemLike[]{
            CAItems.CAKE_BASE,
            CAItems.CAKE_BASE_BAKED,
            CAItems.BIOMASS
    };

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(MODID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> CABlocks.ELECTRIC_MOTOR.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.createaddition.main"))
            .displayItems((itemDisplayParameters, output) -> REGISTRATE.getAll(Registries.ITEM).forEach((item -> {
                for (ItemLike excluded : excludedItemsList) {
                    if (item.is(excluded.asItem())) {
                        output.accept(item.get(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                        return;
                    }
                }
                output.accept(item.get());
            })))
            .build());

    public CreateAddition(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::onRegister);
        eventBus.addListener(RegisterCapabilitiesEvent.class, CACapabilities::register);
        eventBus.addListener(RegisterPayloadHandlersEvent.class, CreateAddition::registerPackets);
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, CARecipes::register);

        //IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //MinecraftForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(this);


        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        //

        IE_ACTIVE = ModList.get().isLoaded("immersiveengineering");
        CC_ACTIVE = ModList.get().isLoaded("computercraft");
        AE2_ACTIVE = ModList.get().isLoaded("ae2");
        MEK_ACTIVE = ModList.get().isLoaded("mekanism");
        SIM_ACTIVE = ModList.get().isLoaded("simulated");

        REGISTRATE.registerEventListeners(eventBus);
        CABlocks.register();
        CABlockEntities.register();
        CAItems.register();
        CREATIVE_MODE_TABS.register(eventBus);
        CAFluids.register();
        CAEffects.register(eventBus);
        CARecipes.register(eventBus);
        CASounds.register(eventBus);
        CASchedule.register();
        CADamageTypes.register();
        CADisplaySources.register();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> CAPartials::init);
    }

    private void setup(final FMLCommonSetupEvent event) {
    	// BlockStressValues.CAPACITIES.registerProvider(MODID, AllConfigs.server().kinetics.stressValues);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    	// event.enqueueWork(CAPonder::register);
        event.enqueueWork(CAItemProperties::register);

        PonderIndex.addPlugin(new CAPonderPlugin());

        RenderType cutout = RenderType.cutoutMipped();

        ItemBlockRenderTypes.setRenderLayer(CABlocks.TESLA_COIL.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(CABlocks.BARBED_WIRE.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(CABlocks.SMALL_LIGHT_CONNECTOR.get(), cutout);
    }

    public void postInit(FMLLoadCompleteEvent evt) {
        //Network.registerMessage(0, ObservePacketLegacy.class, ObservePacketLegacy::encode, ObservePacketLegacy::decode, ObservePacketLegacy::handle);
        //Network.registerMessage(1, EnergyNetworkPacket.class, EnergyNetworkPacket::encode, EnergyNetworkPacket::decode, EnergyNetworkPacket::handle);

        BoilerHeater.REGISTRY.register(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) return -1;
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) return 1;
            return 0;
        });

        //if (MEK_ACTIVE) {
        //    MekanismHeaters.registerHeaters();
        //}

        LOGGER.info("Create Crafts & Additions Initialized!");
    }

    public void onRegister(final RegisterEvent event) {
        CAArmInteractions.register();
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
    	CommandDispatcher<CommandSourceStack> dispather = event.getDispatcher();
    	CCApiCommand.register(dispather);
    }

    private static final String PROTOCOL = "1";
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);
        registrar = registrar.executesOn(HandlerThread.MAIN);
        registrar.playBidirectional(
                ObservePacketPayload.TYPE,
                ObservePacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleObservePayload,
                        ServerPayloadHandler::handleObservePayload
                )
        );

        registrar.playBidirectional(
                EnergyNetworkPacketPayload.TYPE,
                EnergyNetworkPacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleEnergyNetworkPayload,
                        ServerPayloadHandler::handleEnergyNetworkPayload
                )
        );

        registrar.playBidirectional(
                TimeRemainingPacketPayload.TYPE,
                TimeRemainingPacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleTimeRemainingPayload,
                        ServerPayloadHandler::handleTimeRemainingPayload
                )
        );
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
