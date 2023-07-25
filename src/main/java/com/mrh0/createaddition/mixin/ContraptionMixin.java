package com.mrh0.createaddition.mixin;

import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Contraption.class, remap = false)
public abstract class ContraptionMixin {

	@Shadow
	private BlockPos anchor;

	@Inject(
			method = "getBlockEntityNBT",
			at = @At("TAIL"),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	protected void getTileEntityNBT(Level world, BlockPos pos, CallbackInfoReturnable<CompoundTag> info, BlockEntity tileentity, CompoundTag nbt) {
		if (tileentity instanceof ModularAccumulatorBlockEntity && nbt.contains("Controller")) {
			nbt.put("Controller", NbtUtils.writeBlockPos(NbtUtils.readBlockPos(nbt.getCompound("Controller")).subtract(anchor)));
		}
	}

}
