package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.config.Config;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortableEnergyInterfacePeripheral implements IPeripheral {

	private final String type;
	private final PortableEnergyInterfaceBlockEntity tileEntity;

	public PortableEnergyInterfacePeripheral(String type, PortableEnergyInterfaceBlockEntity tileEntity) {
		this.type = type;
		this.tileEntity = tileEntity;
	}

	@NotNull @Override
	public String getType() {
		return this.type;
	}

	@Override
	public boolean equals(@Nullable IPeripheral iPeripheral) {
		return iPeripheral == this;
	}

	@Nullable
	@Override
	public Object getTarget() {
		return this.tileEntity;
	}

	@LuaFunction(mainThread = true)
	public final int getEnergy() {
		return this.tileEntity.getEnergy();
	}

	@LuaFunction(mainThread = true)
	public final int getCapacity() {
		return this.tileEntity.getCapacity();
	}

	@LuaFunction(mainThread = true)
	public boolean isConnected() {
		return this.tileEntity.isConnected();
	}

	@LuaFunction(mainThread = true)
	public int getMaxInsert() {
		return Config.PEI_MAX_INPUT.get();
	}

	@LuaFunction(mainThread = true)
	public int getMaxExtract() {
		return Config.PEI_MAX_OUTPUT.get();
	}
}
