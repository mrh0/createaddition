package com.mrh0.createaddition.compat.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours;
import com.simibubi.create.content.logistics.block.display.DisplayBehaviour;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

@Mixin(value = AllDisplayBehaviours.class, remap = false)
public class AllDisplayBehavioursMixin {
	/*@Overwrite
	public static List<DisplaySource> sourcesOf(BlockEntity tileEntity) {
		var sources = AllDisplayBehaviours.sourcesOf(tileEntity.getType());
		if(!sources.isEmpty()) return sources;
		
		var cap = tileEntity.getCapability(ForgeCapabilities.ENERGY);
		if (cap.isPresent()) {
			return List.of(ForgeEnergyDisplaySource.INSTANCE);
		}
		return List.of();
	}*/
}
