package com.mrh0.createaddition.compat.computercraft;

import java.util.ArrayList;
import java.util.List;

import com.mrh0.createaddition.blocks.servo_motor.ServoMotorBlockEntity;
import com.mrh0.createaddition.config.CommonConfig;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ServoMotorPeripheral implements IPeripheral {
	protected final List<IComputerAccess> connected = new ArrayList<>();
	protected final ServoMotorBlockEntity tileEntity;

	public ServoMotorPeripheral(ServoMotorBlockEntity tileEntity) {
		this.tileEntity = tileEntity;
	}

	@Override
	public String getType() {
		return "servo_motor";
	}

	@Override
	public Object getTarget() {
		return tileEntity;
	}

	@Override
	public void attach(IComputerAccess computer) {
		connected.add(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		connected.remove(computer);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	@LuaFunction(mainThread = true)
	public final float getSpeed() {
		return tileEntity.getRPM();
	}

	@LuaFunction(mainThread = true)
	public final void setSpeed(double rpm) throws LuaException {
		double limit = CommonConfig.ELECTRIC_MOTOR_RPM_RANGE.get();
		if (Math.abs(rpm) > limit)
			throw new LuaException("Speed out of range (max " + (int) limit + " RPM).");
		tileEntity.setRPM((float) rpm);
	}

	@LuaFunction(mainThread = true)
	public final float getAngle() {
		return tileEntity.getCurrentAngle();
	}

	@LuaFunction(mainThread = true)
	public final float getTargetAngle() {
		return tileEntity.getTargetAngle();
	}

	@LuaFunction(mainThread = true)
	public final void setTargetAngle(double angle) throws LuaException {
		if (angle > 180 || angle < -180)
			throw new LuaException("Angle must be between -180 and 180.");
		if (angle >= 0) {
			tileEntity.setMinAngleDegrees(0);
			tileEntity.setMaxAngleDegrees((int) angle);
		} else {
			tileEntity.setMinAngleDegrees((int) Math.abs(angle));
			tileEntity.setMaxAngleDegrees(0);
		}
	}

	@LuaFunction(mainThread = true)
	public final float getMinAngle() {
		return tileEntity.getMinAngleDegrees();
	}

	@LuaFunction(mainThread = true)
	public final void setMinAngle(double degrees) throws LuaException {
		if (degrees < 0 || degrees > 180)
			throw new LuaException("Min angle must be between 0 and 180.");
		tileEntity.setMinAngleDegrees((int) Math.abs(degrees));
	}

	@LuaFunction(mainThread = true)
	public final float getMaxAngle() {
		return tileEntity.getMaxAngleDegrees();
	}

	@LuaFunction(mainThread = true)
	public final void setMaxAngle(double degrees) throws LuaException {
		if (degrees < 0 || degrees > 180)
			throw new LuaException("Max angle must be between 0 and 180.");
		tileEntity.setMaxAngleDegrees((int) degrees);
	}

	@LuaFunction(mainThread = true)
	public final boolean isAssembled() {
		return tileEntity.isRunning();
	}

	@LuaFunction(mainThread = true)
	public final void assemble() {
		if (!tileEntity.isRunning())
			tileEntity.triggerAssemble();
	}

	@LuaFunction(mainThread = true)
	public final void disassemble() {
		if (tileEntity.isRunning())
			tileEntity.disassemble();
	}

	@LuaFunction(mainThread = true)
	public final int getEnergyConsumption() {
		return tileEntity.getEnergyConsumption();
	}
}
