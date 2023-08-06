package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PortableEnergyInterfaceMovement implements MovementBehaviour {

	@Override
	public Vec3 getActiveAreaOffset(MovementContext context) {
		return Vec3.atLowerCornerOf(context.state.getValue(PortableEnergyInterfaceBlock.FACING).getNormal()).scale(1.850000023841858D);
	}

	@Override
	public boolean hasSpecialInstancedRendering() {
		return true;
	}

	@Override
	@Nullable
	public ActorInstance createInstance(MaterialManager materialManager, VirtualRenderWorld simulationWorld, MovementContext context) {
		return new PortableEnergyInterfaceActorInstance(materialManager, simulationWorld, context);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
		if (!ContraptionRenderDispatcher.canInstance()) {
			PortableEnergyInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
		}

	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		boolean onCarriage = context.contraption instanceof CarriageContraption;
		if (!onCarriage || !(context.motion.length() > 0.25D)) {
			if (!this.findInterface(context, pos)) {
				context.data.remove("WorkingPos");
			}

		}
	}

	@Override
	public void tick(MovementContext context) {
		if (context.world.isClientSide) {
			getAnimation(context).tickChaser();
		}

		boolean onCarriage = context.contraption instanceof CarriageContraption;
		if (!onCarriage || !(context.motion.length() > 0.25D)) {
			BlockPos pos;
			if (context.world.isClientSide) {
				pos = new BlockPos((int)context.position.x, (int)context.position.y, (int)context.position.z); // was new BlockPos(context.position)
				if (!this.findInterface(context, pos)) {
					this.reset(context);
				}

			} else if (context.data.contains("WorkingPos")) {
				pos = NbtUtils.readBlockPos(context.data.getCompound("WorkingPos"));
				Vec3 target = VecHelper.getCenterOf(pos);
				if (!context.stall && !onCarriage && context.position.closerThan(target, target.distanceTo(context.position.add(context.motion)))) {
					context.stall = true;
				}

				Optional<Direction> currentFacingIfValid = this.getCurrentFacingIfValid(context);
				if (currentFacingIfValid.isPresent()) {
					PortableEnergyInterfaceBlockEntity stationaryInterface = this.getStationaryInterfaceAt(context.world, pos, context.state, currentFacingIfValid.get());
					if (stationaryInterface == null) {
						this.reset(context);
					} else {
						if (stationaryInterface.getConnectedEntity() == null) {
							stationaryInterface.startTransferringTo(context.contraption, stationaryInterface.getConnectionDistance());
						}
						boolean timerBelow = stationaryInterface.getTransferTimer() <= 4;
						stationaryInterface.keepAlive = 2;
						if (context.stall && timerBelow) {
							context.stall = false;
						}

					}
				}
			}
		}
	}

	protected boolean findInterface(MovementContext context, BlockPos pos) {
		Contraption var4 = context.contraption;
		if (var4 instanceof CarriageContraption) {
			CarriageContraption cc = (CarriageContraption)var4;
			if (!cc.notInPortal()) {
				return false;
			}
		}

		Optional<Direction> currentFacingIfValid = this.getCurrentFacingIfValid(context);
		if (!currentFacingIfValid.isPresent()) {
			return false;
		} else {
			Direction currentFacing = currentFacingIfValid.get();
			PortableEnergyInterfaceBlockEntity psi = this.findStationaryInterface(context.world, pos, context.state, currentFacing);
			if (psi == null) {
				return false;
			} else if (psi.isPowered()) {
				return false;
			} else {
				context.data.put("WorkingPos", NbtUtils.writeBlockPos(psi.getBlockPos()));
				if (!context.world.isClientSide) {
					Vec3 diff = VecHelper.getCenterOf(psi.getBlockPos()).subtract(context.position);
					diff = VecHelper.project(diff, Vec3.atLowerCornerOf(currentFacing.getNormal()));
					float distance = (float)(diff.length() + 1.850000023841858D - 1.0D);
					psi.startTransferringTo(context.contraption, distance);
				} else {
					context.data.put("ClientPrevPos", NbtUtils.writeBlockPos(pos));
					if (context.contraption instanceof CarriageContraption || context.contraption.entity.isStalled() || context.motion.lengthSqr() == 0.0D) {
						getAnimation(context).chase(psi.getConnectionDistance() / 2.0F, 0.25D, LerpedFloat.Chaser.LINEAR);
					}
				}

				return true;
			}
		}
	}

	@Override
	public void stopMoving(MovementContext context) {
	}

	@Override
	public void cancelStall(MovementContext context) {
		this.reset(context);
	}

	public void reset(MovementContext context) {
		context.data.remove("ClientPrevPos");
		context.data.remove("WorkingPos");
		context.stall = false;
		getAnimation(context).chase(0.0D, 0.25D, LerpedFloat.Chaser.LINEAR);
	}

	private PortableEnergyInterfaceBlockEntity findStationaryInterface(Level world, BlockPos pos, BlockState state, Direction facing) {
		for(int i = 0; i < 2; ++i) {
			PortableEnergyInterfaceBlockEntity interfaceAt = this.getStationaryInterfaceAt(world, pos.relative(facing, i), state, facing);
			if (interfaceAt != null) {
				return interfaceAt;
			}
		}

		return null;
	}

	private PortableEnergyInterfaceBlockEntity getStationaryInterfaceAt(Level world, BlockPos pos, BlockState state, Direction facing) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof PortableEnergyInterfaceBlockEntity) {
			PortableEnergyInterfaceBlockEntity psi = (PortableEnergyInterfaceBlockEntity)te;
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() != state.getBlock()) {
				return null;
			} else if (blockState.getValue(PortableEnergyInterfaceBlock.FACING) != facing.getOpposite()) {
				return null;
			} else {
				return psi.isPowered() ? null : psi;
			}
		} else {
			return null;
		}
	}

	private Optional<Direction> getCurrentFacingIfValid(MovementContext context) {
		Vec3 directionVec = Vec3.atLowerCornerOf(context.state.getValue(PortableEnergyInterfaceBlock.FACING).getNormal());
		directionVec = context.rotation.apply(directionVec);
		Direction facingFromVector = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);
		return directionVec.distanceTo(Vec3.atLowerCornerOf(facingFromVector.getNormal())) > 0.5D ? Optional.empty() : Optional.of(facingFromVector);
	}

	public static LerpedFloat getAnimation(MovementContext context) {
		Object var2 = context.temporaryData;
		if (var2 instanceof LerpedFloat) {
			LerpedFloat lf = (LerpedFloat)var2;
			return lf;
		} else {
			LerpedFloat nlf = LerpedFloat.linear();
			context.temporaryData = nlf;
			return nlf;
		}
	}

}
