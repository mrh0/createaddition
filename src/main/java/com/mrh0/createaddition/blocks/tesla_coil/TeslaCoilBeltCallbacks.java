package com.mrh0.createaddition.blocks.tesla_coil;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;

public class TeslaCoilBeltCallbacks {
	public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, TeslaCoilTileEntity te) {
		if(te.getBlockState().getValue(TeslaCoil.FACING) == Direction.UP) {
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		return BeltProcessingBehaviour.ProcessingResult.PASS;
	}

	public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, TeslaCoilTileEntity te) {
		return te.onCharge(transported, handler);
	}
}