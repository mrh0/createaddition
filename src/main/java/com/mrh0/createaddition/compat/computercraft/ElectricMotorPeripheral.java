package com.mrh0.createaddition.compat.computercraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;

import com.mrh0.createaddition.config.Config;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ElectricMotorPeripheral implements IPeripheral {
	protected final List<IComputerAccess> connected = new ArrayList<>();
    protected String type;
    protected ElectricMotorBlockEntity tileEntity;

    public ElectricMotorPeripheral(String type, ElectricMotorBlockEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connected;
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
    public boolean equals(IPeripheral iPeripheral) {
        return iPeripheral == this;
    }
    
    @LuaFunction
    public final String getType() {
        return type;
    }
    
    @LuaFunction(mainThread = true)
    public final void setSpeed(double rpm) throws LuaException {
    	if ((float) rpm == getSpeed())
    		return;
    	if(tileEntity != null) {
    		if(!tileEntity.setRPM( (float) rpm))
    			throw new LuaException("Speed is set too many times per second (Anti Spam).");
    	}
    }
    
    @LuaFunction(mainThread = true)
    public final void stop() throws LuaException {
    	setSpeed(0);
    }
    
    @LuaFunction(mainThread = true)
    public final float getSpeed() throws LuaException {
    	if(tileEntity != null)
    		return tileEntity.getRPM();
    	return 0;
    }
    
    @LuaFunction(mainThread = true)
    public final int getStressCapacity() throws LuaException {
    	if(tileEntity != null)
    		return tileEntity.getGeneratedStress();
    	return 0;
    }
    
    @LuaFunction(mainThread = true)
    public final float getEnergyConsumption() throws LuaException {
    	if(tileEntity != null)
    		return tileEntity.getEnergyConsumption();
    	return 0;
    }
    
    @LuaFunction(mainThread = true)
    public final float rotate(double deg, Optional<Double> rpm) throws LuaException {
    	if(tileEntity != null) {
    		double _rpm = rpm.orElse((double) getSpeed());
    		if(rpm.isPresent())
    			setSpeed(deg < 0 ? -_rpm : _rpm);
    		return ElectricMotorBlockEntity.getDurationAngle((float)deg, 0, (float)_rpm) / 20f;
    	}
    	return 0f;
    }
    
    @LuaFunction(mainThread = true)
    public final float translate(double dist, Optional<Double> rpm) throws LuaException {
    	if(tileEntity != null) {
    		double _rpm = rpm.orElse((double) getSpeed());
    		if(rpm.isPresent())
    			setSpeed(dist < 0 ? -_rpm : _rpm);
    		return ElectricMotorBlockEntity.getDurationDistance((float)dist, 0, (float)_rpm) / 20f;
    	}
    	return 0f;
    }

    @LuaFunction(mainThread = true)
    public long getMaxInsert() {
        return Config.ELECTRIC_MOTOR_MAX_INPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getMaxExtract() {
        return 0;
    }
}
