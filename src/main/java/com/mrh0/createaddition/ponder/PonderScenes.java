package com.mrh0.createaddition.ponder;

import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.blocks.connector.base.ConnectorMode;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


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

		Vec3 blockSurface = util.vector.blockSurface(motor, Direction.EAST);
		AABB point = new AABB(blockSurface, blockSurface);
		AABB expanded = point.inflate(1 / 16f, 1 / 5f, 1 / 5f);

		scene.overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, point, 1);
		scene.idle(1);
		scene.overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, expanded, 60);
		scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).scroll(), 60);
		scene.idle(20);

		scene.addKeyframe();
		scene.overlay.showText(70)
			.text("Scrolling on the back panel changes the RPM of the motors' rotational output")
			.placeNearTarget()
			.pointAt(blockSurface);
		scene.idle(10);
		scene.world.modifyKineticSpeed(util.select.fromTo(1, 1, 2, 3, 1, 2), f -> 4 * f);
		scene.effects.rotationSpeedIndicator(motor);
		scene.idle(70);

		scene.addKeyframe();
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
		scene.markAsFinished();


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
		scene.markAsFinished();
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

		scene.addKeyframe();
		scene.overlay.showControls(new InputWindowElement(util.vector.topOf(mill), Pointing.DOWN).rightClick(), 50);
		scene.overlay.showText(50)
		.text("Manualy retrieve the rolled output by R-clicking the Mill")
		.placeNearTarget()
		.pointAt(util.vector.topOf(mill));
		scene.idle(60);
		scene.markAsFinished();
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

		scene.addKeyframe();
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
		scene.markAsFinished();
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

		scene.addKeyframe();
		scene.idle(10);
		scene.overlay.showText(150)
			.text("Get to the API documentation by issuing the command '/cca_api' in the chat")
			.placeNearTarget()
			.pointAt(util.vector.topOf(computer));
		scene.idle(160);
		scene.markAsFinished();
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
		scene.idle(5);
		Vec3 topOf = util.vector.topOf(depotPos);
		scene.overlay.showText(50)
			.attachKeyFrame()
			.text("Tesla Coil will charge Items below it")
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(60);

		scene.world.createItemOnBeltLike(depotPos, Direction.NORTH, AllItems.CHROMATIC_COMPOUND.asStack());
		scene.idle(10);
		scene.world.setBlock(util.grid.at(2, 3, 2), CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoilBlock.FACING, Direction.UP).setValue(TeslaCoilBlock.POWERED, true), false);
		scene.overlay.showText(70)
			.attachKeyFrame()
			.text("It will charge any Forge Energy Items and more!")
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void teslaCoilHurt(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("tesla_coil_hurt", "Dangerous Tesla Coils");
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		//scene.world.setBlock(util.grid.at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos teslacoil = util.grid.at(2, 1, 2);
		BlockPos lever = util.grid.at(2, 1, 1);
		scene.world.showSection(util.select.position(teslacoil), Direction.DOWN);
		scene.idle(5);
		scene.overlay.showText(70)
			.attachKeyFrame()
			.text("The Tesla Coil is also able to Shock nearby Players and Mobs")
			.placeNearTarget()
			.pointAt(util.vector.topOf(teslacoil));
		scene.idle(80);
		scene.world.showSection(util.select.position(lever), Direction.SOUTH);

		scene.idle(5);
		scene.overlay.showText(50)
			.attachKeyFrame()
			.text("This can be activated with a Redstone signal")
			.placeNearTarget()
			.pointAt(util.vector.centerOf(lever));
		scene.idle(60);
		scene.world.setBlock(lever, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.POWERED, true).setValue(LeverBlock.FACING, Direction.SOUTH).setValue(LeverBlock.FACE, AttachFace.FLOOR), false);
		scene.idle(5);
		scene.world.setBlock(teslacoil, CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoilBlock.FACING, Direction.DOWN).setValue(TeslaCoilBlock.POWERED, true), false);
		scene.idle(5);
		scene.overlay.showText(70)
			.attachKeyFrame()
			.text("Prepare to be Shocked!")
			.placeNearTarget()
			.pointAt(util.vector.topOf(teslacoil));
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void liquidBlazeBurner(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("liquid_blaze_burner", "Liquid Fuel Burning");
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		//scene.world.setBlock(util.grid.at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos burner = util.grid.at(2, 1, 2);
		BlockPos[] blocks = {
				util.grid.at(1, 1, 2),
				util.grid.at(0, 1, 2),
				util.grid.at(0, 2, 2),
				util.grid.at(0, 3, 2)
		};
		scene.world.showSection(util.select.position(burner), Direction.DOWN);
		scene.idle(5);
		scene.overlay.showText(50)
		.attachKeyFrame()
		.text("Giving the Blaze Burner a Straw")
		.placeNearTarget()
		.pointAt(util.vector.topOf(burner));
		scene.idle(10);
		scene.overlay.showControls(new InputWindowElement(util.vector.topOf(burner), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(CAItems.STRAW.get())), 40);
		scene.world.setBlock(burner, CABlocks.LIQUID_BLAZE_BURNER.getDefaultState().setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING), false);
		scene.idle(60);
		scene.overlay.showText(50)
			.attachKeyFrame()
			.text("will allow it to accept liquid fuels by Buckets,")
			.placeNearTarget()
			.pointAt(util.vector.topOf(burner));
		scene.idle(10);
		scene.overlay.showControls(new InputWindowElement(util.vector.topOf(burner), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(CAFluids.BIOETHANOL.getBucket().get())), 40);
		scene.idle(60);
		scene.overlay.showText(50)
			.text("- or by pipes.")
			.placeNearTarget()
			.pointAt(util.vector.topOf(burner));
		scene.idle(10);

		for (int i = 0; i < blocks.length; i++) {
			scene.idle(5);
			scene.world.showSection(util.select.position(blocks[i]), Direction.EAST);
		}
		scene.idle(20);
		scene.markAsFinished();
	}


	public static void modularAccumulator(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("modular_accumulator", "Accumulator");
		scene.configureBasePlate(0, 0, 4);
		scene.showBasePlate();
		scene.idle(15);

		BlockPos cIn = new BlockPos(1, 3, 1);
		BlockPos cOut = new BlockPos(2, 3, 2);

		var accumulator = util.select.fromTo(1, 1, 1, 2, 2, 2);
		//scene.world.showSection(accumulator, Direction.EAST);
		ElementLink<WorldSectionElement> accumulatorLink = scene.world.showIndependentSection(accumulator, Direction.EAST);
		scene.idle(15);
		scene.overlay.showOutline(PonderPalette.GREEN, accumulatorLink, accumulator, 50);

		scene.overlay.showText(50)
			.text("The Accumulator is a multiblock")
			.placeNearTarget()
			.pointAt(util.vector.centerOf(cIn));
		scene.idle(60);
		scene.overlay.showText(50)
			.text("It can store large amounts of energy")
			.placeNearTarget()
			.pointAt(util.vector.centerOf(cIn));
		scene.idle(60);
		scene.world.showSection(util.select.position(cIn), Direction.DOWN);
		scene.idle(5);
		scene.world.showSection(util.select.position(cOut), Direction.DOWN);

		scene.idle(15);
		scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(cIn), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(AllItems.WRENCH.get())), 40);
		scene.world.setBlock(cIn, CABlocks.SMALL_CONNECTOR.getDefaultState().setValue(AbstractConnectorBlock.FACING, Direction.DOWN).setValue(AbstractConnectorBlock.MODE, ConnectorMode.Push), false);
		scene.overlay.showText(50)
			.attachKeyFrame()
			.text("Configure an input connector,")
			.placeNearTarget()
			.pointAt(util.vector.centerOf(cIn));

		scene.idle(60);
		scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(cOut), Pointing.DOWN).rightClick()
				.withItem(new ItemStack(AllItems.WRENCH.get())), 40);
		scene.world.setBlock(cOut, CABlocks.SMALL_CONNECTOR.getDefaultState().setValue(AbstractConnectorBlock.FACING, Direction.DOWN).setValue(AbstractConnectorBlock.MODE, ConnectorMode.Pull), false);
		scene.overlay.showText(50)
			.text("and an output connector.")
			.placeNearTarget()
			.pointAt(util.vector.centerOf(cOut));
		scene.idle(60);

		scene.overlay.showText(110)
		.text("Compat")
		.placeNearTarget()
		.pointAt(util.vector.centerOf(cOut));
		scene.idle(120);
		scene.markAsFinished();
	}

	public static void peiTransfer(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("pei_transfer", "Contraption Storage Exchange");
		scene.configureBasePlate(0, 0, 6);
		scene.scaleSceneView(0.95f);
		scene.setSceneOffsetY(-1);
		scene.world.showSection(util.select.layer(0), Direction.UP);
		scene.idle(5);

		BlockPos bearing = util.grid.at(5, 1, 2);
		scene.world.showSection(util.select.position(bearing), Direction.DOWN);
		scene.idle(5);
		ElementLink<WorldSectionElement> contraption =
			scene.world.showIndependentSection(util.select.fromTo(5, 2, 2, 6, 4, 2), Direction.DOWN);
		scene.world.configureCenterOfRotation(contraption, util.vector.centerOf(bearing));
		scene.idle(10);
		scene.world.rotateBearing(bearing, 360, 70);
		scene.world.rotateSection(contraption, 0, 360, 0, 70);
		scene.overlay.showText(60)
			.pointAt(util.vector.topOf(bearing.above(2)))
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Inventories on moving contraptions cannot be accessed by players.");

		scene.idle(70);
		BlockPos pei = util.grid.at(4, 2, 2);
		scene.world.showSectionAndMerge(util.select.position(pei), Direction.EAST, contraption);
		scene.idle(13);
		scene.effects.superGlue(pei, Direction.EAST, true);

		scene.overlay.showText(80)
			.pointAt(util.vector.topOf(pei))
			.colored(PonderPalette.GREEN)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This component can interact with storage without the need to stop the contraption.");
		scene.idle(90);

		BlockPos pei2 = pei.west(2);
		scene.world.showSection(util.select.position(pei2), Direction.DOWN);
		scene.overlay.showSelectionWithText(util.select.position(pei.west()), 50)
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Place a second one with a gap of 1 or 2 blocks inbetween");
		scene.idle(55);

		scene.world.rotateBearing(bearing, 360, 60);
		scene.world.rotateSection(contraption, 0, 360, 0, 60);
		scene.idle(20);

		scene.overlay.showText(40)
			.placeNearTarget()
			.pointAt(util.vector.of(3, 3, 2.5))
			.text("Whenever they pass by each other, they will engage in a connection");
		scene.idle(35);

		Selection both = util.select.fromTo(2, 2, 2, 4, 2, 2);
		Class<PortableEnergyInterfaceBlockEntity> peiClass = PortableEnergyInterfaceBlockEntity.class;

		scene.world.modifyBlockEntityNBT(both, peiClass, nbt -> {
			nbt.putFloat("Distance", 1);
			nbt.putFloat("Timer", 40);
		});

		scene.idle(20);
		scene.overlay.showOutline(PonderPalette.GREEN, pei, util.select.fromTo(5, 3, 2, 6, 4, 2), 80);
		scene.idle(10);

		scene.overlay.showSelectionWithText(util.select.position(pei2), 70)
			.placeNearTarget()
			.colored(PonderPalette.GREEN)
			.attachKeyFrame()
			.text("While engaged, the stationary interface will represent ALL inventories on the contraption");

		scene.idle(80);

		BlockPos connector = util.grid.at(2, 3, 2);
		scene.world.showSection(util.select.position(connector), Direction.DOWN);
		scene.overlay.showText(70)
			.placeNearTarget()
			.pointAt(util.vector.centerOf(connector))
			.attachKeyFrame()
			.text("Items can now be inserted...");
		scene.idle(80);

		scene.overlay.showText(120)
			.placeNearTarget()
			.pointAt(util.vector.centerOf(pei2))
			.text("After no items have been exchanged for a while, the contraption will continue on its way");
		scene.world.modifyBlockEntityNBT(both, peiClass, nbt -> nbt.putFloat("Timer", 9));

		scene.idle(15);
		scene.world.rotateBearing(bearing, 270, 120);
		scene.world.rotateSection(contraption, 0, 270, 0, 120);
		scene.markAsFinished();
	}

	public static void peiRedstone(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("pei_redstone", "Redstone Control");
		scene.configureBasePlate(0, 0, 5);
		scene.setSceneOffsetY(-1);

		Class<PortableEnergyInterfaceBlockEntity> peiClass = PortableEnergyInterfaceBlockEntity.class;
		Selection peis = util.select.fromTo(1, 1, 3, 1, 3, 3);
		scene.world.modifyBlockEntityNBT(peis, peiClass, nbt -> {
			nbt.putFloat("Distance", 1);
			nbt.putFloat("Timer", 40);
		});

		scene.world.showSection(util.select.layer(0), Direction.UP);
		scene.idle(5);
		scene.world.showSection(util.select.layer(1), Direction.DOWN);
		scene.idle(5);

		ElementLink<WorldSectionElement> contraption =
			scene.world.showIndependentSection(util.select.layersFrom(2), Direction.DOWN);
		BlockPos bearing = util.grid.at(3, 1, 3);
		scene.world.configureCenterOfRotation(contraption, util.vector.topOf(bearing));
		scene.idle(20);
		scene.world.modifyBlockEntityNBT(peis, peiClass, nbt -> nbt.putFloat("Timer", 9));
		scene.idle(20);
		scene.world.rotateBearing(bearing, 360 * 3 + 270, 240 + 60);
		scene.world.rotateSection(contraption, 0, 360 * 3 + 270, 0, 240 + 60);
		scene.idle(20);

		scene.world.toggleRedstonePower(util.select.fromTo(1, 1, 1, 1, 1, 2));
		scene.effects.indicateRedstone(util.grid.at(1, 1, 1));

		scene.idle(10);

		scene.overlay.showSelectionWithText(util.select.position(1, 1, 3), 120)
			.colored(PonderPalette.RED)
			.text("Redstone power will prevent the stationary interface from engaging");

		scene.idle(20);
		scene.markAsFinished();
	}
}
