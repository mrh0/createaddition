package com.mrh0.createaddition.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Contraption.class, remap = false)
public abstract class ContraptionMixin {

	/*
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
	*/
}
