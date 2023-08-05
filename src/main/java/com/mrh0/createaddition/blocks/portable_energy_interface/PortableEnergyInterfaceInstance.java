package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.api.instance.TickableInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PortableEnergyInterfaceInstance extends BlockEntityInstance<PortableEnergyInterfaceBlockEntity> implements DynamicInstance, TickableInstance {

	private final PIInstance instance;

	public PortableEnergyInterfaceInstance(MaterialManager materialManager, PortableEnergyInterfaceBlockEntity tile) {
		super(materialManager, tile);

		instance = new PIInstance(materialManager, blockState, getInstancePosition());
	}

	@Override
	public void init() {
		instance.init(isLit());
	}

	@Override
	public void tick() {
		instance.tick(isLit());
	}

	@Override
	public void beginFrame() {
		instance.beginFrame(blockEntity.getExtensionDistance(AnimationTickHolder.getPartialTicks()));
	}

	@Override
	public void updateLight() {
		relight(pos, instance.middle, instance.top);
	}

	@Override
	public void remove() {
		instance.remove();
	}

	private boolean isLit() {
		return blockEntity.isConnected();
	}

	// I have no idea what PI stands for, but I'm guessing it's Portable Interface, so it works
	// for PORTABLE energy INTERFACE I guess.
	public static class PIInstance {
		private final MaterialManager materialManager;
		private final BlockState blockState;
		private final BlockPos instancePos;
		private final float angleX;
		private final float angleY;
		private boolean lit;
		ModelData middle;
		ModelData top;

		public PIInstance(MaterialManager materialManager, BlockState blockState, BlockPos instancePos) {
			this.materialManager = materialManager;
			this.blockState = blockState;
			this.instancePos = instancePos;
			Direction facing = blockState.getValue(PortableEnergyInterfaceBlock.FACING);
			this.angleX = facing == Direction.UP ? 0.0F : (facing == Direction.DOWN ? 180.0F : 90.0F);
			this.angleY = AngleHelper.horizontalAngle(facing);
		}

		public void init(boolean lit) {
			this.lit = lit;
			PartialModel middleForState = lit ? CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE_POWERED : CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE;
			this.middle = this.materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(middleForState, this.blockState).createInstance();
			this.top = this.materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(CAPartials.PORTABLE_ENERGY_INTERFACE_TOP, this.blockState).createInstance();
		}

		public void beginFrame(float progress) {
			this.middle.loadIdentity().translate(this.instancePos).centre().rotateY(this.angleY).rotateX(this.angleX).unCentre();
			this.top.loadIdentity().translate(this.instancePos).centre().rotateY(this.angleY).rotateX(this.angleX).unCentre();
			this.middle.translate(0.0D, progress * 0.5F + 0.375F, 0.0D);
			this.top.translate(0.0D, progress, 0.0D);
		}

		public void tick(boolean lit) {
			if (this.lit != lit) {
				this.lit = lit;
				PartialModel middleForState = lit ? CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE_POWERED : CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE;
				this.materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(middleForState, this.blockState).stealInstance(this.middle);
			}

		}

		public void remove() {
			this.middle.delete();
			this.top.delete();
		}
	}

}
