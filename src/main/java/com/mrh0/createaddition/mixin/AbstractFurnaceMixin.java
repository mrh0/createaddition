package com.mrh0.createaddition.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;

@Mixin(AbstractFurnaceTileEntity.class)
public interface AbstractFurnaceMixin {
	@Accessor
	IIntArray getDataAccess();
}
