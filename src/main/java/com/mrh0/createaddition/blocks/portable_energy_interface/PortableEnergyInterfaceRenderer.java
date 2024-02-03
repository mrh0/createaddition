package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class PortableEnergyInterfaceRenderer extends SafeBlockEntityRenderer<PortableEnergyInterfaceBlockEntity> {

	public PortableEnergyInterfaceRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(PortableEnergyInterfaceBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		if (Backend.canUseInstancing(te.getLevel())) return;
		BlockState blockState = te.getBlockState();
		float progress = te.getExtensionDistance(partialTicks);
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());
		render(blockState, te.isConnected(), progress, null, (sbb) -> sbb.light(light).renderInto(ms, vb));
	}

	public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
		BlockState blockState = context.state;
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());
		float renderPartialTicks = AnimationTickHolder.getPartialTicks();
		LerpedFloat animation = PortableEnergyInterfaceMovement.getAnimation(context);
		float progress = animation.getValue(renderPartialTicks);
		boolean lit = animation.settled();
		render(blockState, lit, progress, matrices.getModel(), (sbb) -> sbb.light(matrices.getWorld(),
				ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
				.renderInto(matrices.getViewProjection(), vb));
	}

	private static void render(BlockState blockState, boolean lit, float progress, PoseStack local, Consumer<SuperByteBuffer> drawCallback) {
		PartialModel middleForState = lit ? CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE_POWERED : CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE;
		SuperByteBuffer middle = CachedBufferer.partial(middleForState, blockState);
		SuperByteBuffer top = CachedBufferer.partial(CAPartials.PORTABLE_ENERGY_INTERFACE_TOP, blockState);
		if (local != null) {
			middle.transform(local);
			top.transform(local);
		}

		Direction facing = blockState.getValue(PortableEnergyInterfaceBlock.FACING);
		rotateToFacing(middle, facing);
		rotateToFacing(top, facing);
		middle.translate(0.0D, progress * 0.5F + 0.375F, 0.0D);
		top.translate(0.0D, progress, 0.0D);
		drawCallback.accept(middle);
		drawCallback.accept(top);
	}

	private static void rotateToFacing(SuperByteBuffer buffer, Direction facing) {
		buffer.centre().rotateY(AngleHelper.horizontalAngle(facing)).rotateX(facing == Direction.UP ? 0.0D : (facing == Direction.DOWN ? 180.0D : 90.0D)).unCentre();
	}

	static PortableEnergyInterfaceBlockEntity getTargetPSI(MovementContext context) {
		String _workingPos_ = "WorkingPos";
		if (!context.data.contains(_workingPos_)) return null;
		BlockPos pos = NbtUtils.readBlockPos(context.data.getCompound(_workingPos_));
		BlockEntity tileEntity = context.world.getBlockEntity(pos);
		if (tileEntity instanceof PortableEnergyInterfaceBlockEntity psi) return !psi.isTransferring() ? null : psi;
		return null;
	}
}
