package com.mrh0.createaddition.ponder;

import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoil;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.content.PonderPalette;
import com.simibubi.create.foundation.ponder.elements.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class PonderScenes {
	public static void electricMotor(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("electric_motor", "Generating Rotational Force using Electric Motors");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos motor = util.grid.at(3, 1, 2);

		for (int i = 0; i < 3; i++) {
			scene.idle(5);
			scene.world.showSection(util.select.position(1 + i, 1, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.effects.rotationSpeedIndicator(motor);
		scene.overlay.showText(50)
			.text("Electric Motors are a compact and configurable source of Rotational Force")
			.placeNearTarget()
			.pointAt(util.vector.topOf(motor));
		scene.idle(50);

		scene.rotateCameraY(90);
		scene.idle(20);

		Vector3d blockSurface = util.vector.blockSurface(motor, Direction.EAST);
		AxisAlignedBB point = new AxisAlignedBB(blockSurface, blockSurface);
		AxisAlignedBB expanded = point.inflate(1 / 16f, 1 / 5f, 1 / 5f);

		scene.overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, point, 1);
		scene.idle(1);
		scene.overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, expanded, 60);
		scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).scroll(), 60);
		scene.idle(20);

		scene.overlay.showText(70)
			.text("Scrolling on the back panel changes the RPM of the motors' rotational output")
			.placeNearTarget()
			.pointAt(blockSurface);
		scene.idle(10);
		scene.world.modifyKineticSpeed(util.select.fromTo(1, 1, 2, 3, 1, 2), f -> 4 * f);
		scene.effects.rotationSpeedIndicator(motor);
		scene.idle(70);
		
		scene.overlay.showText(70)
		.text("The Electric Motor requires a source of energy (fe)")
		.placeNearTarget()
		.pointAt(blockSurface);
		scene.idle(80);
		
		scene.overlay.showText(70)
		.text("The motors' energy consumption is determined by the set RPM")
		.placeNearTarget()
		.pointAt(blockSurface);
		scene.idle(80);
		
		
		scene.rotateCameraY(-90);
	}
	
	public static void alternator(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("alternator", "Generating Electric energy using a Alternator");
		scene.configureBasePlate(1, 0, 4);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos generator = util.grid.at(3, 1, 2);

		for (int i = 0; i < 6; i++) {
			scene.idle(5);
			scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
			//scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.overlay.showText(50)
			.text("The Alternator generates electric energy (fe) from rotational force")
			.placeNearTarget()
			.pointAt(util.vector.topOf(generator));
		scene.idle(60);

		scene.overlay.showText(50)
			.text("It requires atleast 32 RPM to operate")
			.placeNearTarget()
			.pointAt(util.vector.topOf(generator));
		scene.idle(60);

		
		scene.overlay.showText(50)
		.text("The Alternators energy production is determined by the input RPM")
		.placeNearTarget()
		.pointAt(util.vector.topOf(generator));
		scene.idle(60);
	}
	
	public static void rollingMill(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("rolling_mill", "Rolling metals into Rods and Wires");
		scene.configureBasePlate(1, 0, 4);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos mill = util.grid.at(3, 1, 2);

		for (int i = 0; i < 6; i++) {
			scene.idle(5);
			scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.overlay.showText(50)
			.text("The Rolling Mill uses rotational force to roll metals into Rods and Wires")
			.placeNearTarget()
			.pointAt(util.vector.topOf(mill));
		scene.idle(60);

		scene.overlay.showText(50)
			.text("To manualy input items, drop Ingots or Plates on the top of the Mill")
			.placeNearTarget()
			.pointAt(util.vector.topOf(mill));
		scene.idle(60);

		
		scene.overlay.showControls(new InputWindowElement(util.vector.topOf(mill), Pointing.DOWN).rightClick(), 50);
		scene.overlay.showText(50)
		.text("Manualy retrieve the rolled output by R-clicking the Mill")
		.placeNearTarget()
		.pointAt(util.vector.topOf(mill));
		scene.idle(60);
	}
	
	public static void automateRollingMill(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("automated_rolling_mill", "Automating the Rolling Mill");
		scene.configureBasePlate(1, 0, 4);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos mill = util.grid.at(3, 2, 2);
		BlockPos in = util.grid.at(3, 2, 3);
		BlockPos out = util.grid.at(3, 2, 1);
		
		//BlockPos entryBeltPos = util.grid.at(3, 1, 4);
		//BlockPos exitBeltPos = util.grid.at(3, 1, 0);

		for (int i = 0; i < 3; i++) {
			scene.idle(5);
			scene.world.showSection(util.select.position(i, 1, 4), Direction.DOWN);
		}
		
		for (int i = 5; i >= 0; i--) {
			scene.idle(5);
			scene.world.showSection(util.select.position(3, 1, i), Direction.DOWN);
			//scene.world.showSection(util.select.position(3, 2, i), Direction.DOWN);
			scene.world.showSection(util.select.position(4, 1, i), Direction.DOWN);
			scene.world.showSection(util.select.position(4, 2, i), Direction.DOWN);
		}
		
		scene.world.showSection(util.select.position(mill), Direction.DOWN);
		
		
		
		scene.overlay.showText(50)
		.text("The Rolling Mill can be automated using a Belt and two Funnels")
		.placeNearTarget()
		.pointAt(util.vector.topOf(mill));
		scene.idle(60);

		scene.idle(5);
		scene.world.showSection(util.select.position(in), Direction.NORTH);
		scene.idle(5);
		scene.world.showSection(util.select.position(out), Direction.SOUTH);
		scene.idle(20);

		/*ItemStack itemStack = new ItemStack(AllItems.COPPER_INGOT.get());

		for (int i = 0; i < 8; i++) {
			scene.idle(8);
			scene.world.removeItemsFromBelt(exitBeltPos);
			scene.world.flapFunnel(out, false);
			if (i == 2)
				scene.rotateCameraY(70);
			if (i < 6)
				scene.world.createItemOnBelt(entryBeltPos, Direction.EAST, itemStack);
		}
		scene.idle(40);*/
	}
	
	public static void heater(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("heater", "Using electric energy to heat a furnace");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos furnace = util.grid.at(2, 1, 2);
		BlockPos heater = util.grid.at(2, 2, 2);
		BlockPos connector = util.grid.at(2, 3, 2);
		
		scene.idle(5);
		scene.world.showSection(util.select.position(furnace), Direction.DOWN);
		scene.idle(5);
		scene.world.showSection(util.select.position(heater), Direction.DOWN);
		
		scene.idle(10);
		scene.overlay.showText(50)
			.text("The heater can be placed on any side facing the furnace")
			.placeNearTarget()
			.pointAt(util.vector.topOf(heater));
		scene.idle(60);
		
		scene.world.showSection(util.select.position(connector), Direction.DOWN);
		scene.idle(10);
		scene.overlay.showText(50)
			.text("When connected to a sufficient source of electric energy (fe), the Heater will light the Furnace")
			.placeNearTarget()
			.pointAt(util.vector.topOf(heater));
		scene.idle(60);
		
		scene.world.setBlocks(util.select.position(furnace), Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.LIT, true), false);
		scene.overlay.showText(50)
		.text("The Furnace will stay lit as long as enough energy is provided")
		.placeNearTarget()
		.pointAt(util.vector.blockSurface(furnace, Direction.NORTH));
		scene.idle(60);
	}
	
	public static void ccMotor(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("cc_electric_motor", "Using Computercraft to control an Electric Motor");
		scene.configureBasePlate(0, 0, 5);
		scene.world.showSection(util.select.layer(0), Direction.UP);

		BlockPos motor = util.grid.at(2, 1, 2);
		BlockPos computer = util.grid.at(1, 1, 2);
		
		scene.idle(5);
		scene.world.showSection(util.select.position(motor), Direction.DOWN);
		scene.idle(5);
		scene.world.showSection(util.select.position(computer), Direction.DOWN);
		
		scene.idle(10);
		scene.overlay.showText(50)
			.text("The Electric Motor can be controlled using Computercraft")
			.placeNearTarget()
			.pointAt(util.vector.topOf(computer));
		scene.idle(60);
		
		scene.idle(10);
		scene.overlay.showText(50)
			.text("Connect to the motor using 'peripheral.wrap(side)'")
			.placeNearTarget()
			.pointAt(util.vector.topOf(computer));
		scene.idle(60);
		
		scene.idle(10);
		scene.overlay.showText(150)
			.text("Get to the API documentation by issuing the command '/cca_api' in the chat")
			.placeNearTarget()
			.pointAt(util.vector.topOf(computer));
		scene.idle(160);
	}
	
	public static void teslaCoil(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("tesla_coil", "Using Tesla Coil");
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		scene.world.setBlock(util.grid.at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos depotPos = util.grid.at(2, 1, 2);
		scene.world.showSection(util.select.position(2, 1, 2), Direction.DOWN);
		scene.idle(5);
		scene.world.showSection(util.select.position(2, 3, 2), Direction.DOWN);
		
		Vector3d topOf = util.vector.topOf(depotPos);
		scene.overlay.showText(60)
			.attachKeyFrame()
			.text("Depots can serve as 'stationary' belt elements")
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(70);

		scene.overlay.showControls(new InputWindowElement(topOf, Pointing.DOWN).rightClick()
			.withItem(AllItems.CHROMATIC_COMPOUND.asStack()), 20);
		scene.idle(7);
		scene.world.createItemOnBeltLike(depotPos, Direction.NORTH, AllItems.CHROMATIC_COMPOUND.asStack());
		scene.idle(10);
		scene.world.setBlock(util.grid.at(2, 3, 2), CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoil.FACING, Direction.UP).setValue(TeslaCoil.POWERED, true), false);
		scene.overlay.showText(70)
			.attachKeyFrame()
			.text("Right-Click to manually place or remove Items from it")
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(80);
	}
}
