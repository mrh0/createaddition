package com.mrh0.createaddition.compat.simulated;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SIMCapabilities {
    public static void registerBECapabilities(RegisterCapabilitiesEvent event) {

        var dockType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(
                ResourceKey.create(
                        Registries.BLOCK_ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("simulated", "docking_connector")
                )
        );

        if (dockType == null) {
            return;
        }
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                dockType,
                (be, side) -> ((DockingConnectorBEAccess) be).getEnergyStorage()
        );
    }
}
