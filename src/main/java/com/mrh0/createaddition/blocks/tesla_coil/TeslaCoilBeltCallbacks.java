package com.mrh0.createaddition.blocks.tesla_coil;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;

import net.minecraft.util.Direction;

import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;

public class TeslaCoilBeltCallbacks {

	static ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, TeslaCoilTileEntity te) {
		if(te.getBlockState().getValue(TeslaCoil.FACING) == Direction.UP) {
			return ProcessingResult.HOLD;
		}
		return ProcessingResult.PASS;
	}

	static ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, TeslaCoilTileEntity te) {
		return te.onCharge(transported, handler);
	}
}