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
    public final void setSpeed(int rpm) throws LuaException {
    	if(rpm == getSpeed())
    		return;
    	if(tileEntity != null) {
    		if(!tileEntity.setRPM(rpm))
    			throw new LuaException("Speed is set too many times per second (Anti Spam).");
    	}
    }
    
    @LuaFunction(mainThread = true)
    public final void stop() throws LuaException {
    	setSpeed(0);
    }
    
    @LuaFunction(mainThread = true)
    public final int getSpeed() throws LuaException {
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
    public final int getEnergyConsumption() throws LuaException {
    	if(tileEntity != null)
    		return tileEntity.getEnergyConsumption();
    	return 0;
    }
    
    @LuaFunction(mainThread = true)
    public final float rotate(int deg, Optional<Integer> rpm) throws LuaException {
    	if(tileEntity != null) {
    		int _rpm = rpm.orElse(getSpeed());
    		if(rpm.isPresent())
    			setSpeed(deg < 0 ? -_rpm : _rpm);
    		return ElectricMotorBlockEntity.getDurationAngle(deg, 0, _rpm) / 20f;
    	}
    	return 0f;
    }
    
    @LuaFunction(mainThread = true)
    public final float translate(int blocks, Optional<Integer> rpm) throws LuaException {
    	if(tileEntity != null) {
    		int _rpm = rpm.orElse(getSpeed());
    		if(rpm.isPresent())
    			setSpeed(blocks < 0 ? -_rpm : _rpm);
    		return ElectricMotorBlockEntity.getDurationDistance(blocks, 0, _rpm) / 20f;
    	}
    	return 0f;
    }

    @LuaFunction(mainThread = true)
    public int getMaxInsert() {
        return Config.ELECTRIC_MOTOR_MAX_INPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getMaxExtract() {
        return 0;
    }
}
