package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;

public class ElectricMotorTileEntity extends GeneratingKineticTileEntity {

	public static final int DEFAULT_SPEED = 128;
	protected ScrollValueBehaviour generatedSpeed;

	public ElectricMotorTileEntity(TileEntityType<? extends ElectricMotorTileEntity> type) {
		super(type);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		Integer max = AllConfigs.SERVER.kinetics.maxMotorSpeed.get();

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.get(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
		generatedSpeed.between(-max, max);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.scrollableValue = DEFAULT_SPEED;
		generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		generatedSpeed.withStepFunction(CreativeMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.ELECTRIC_MOTOR.has(getBlockState()))
			return 0;
		return convertToDirection(generatedSpeed.getValue(), getBlockState().get(ElectricMotorBlock.FACING));
	}
	
	@Override
	protected Block getStressConfigKey() {
		System.out.println(AllConfigs.SERVER.kinetics.stressValues.getCapacityOf(CABlocks.ELECTRIC_MOTOR.get()));
		return CABlocks.ELECTRIC_MOTOR.get();
	}
}