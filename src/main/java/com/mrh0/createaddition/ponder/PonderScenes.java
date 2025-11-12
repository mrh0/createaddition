package com.mrh0.createaddition.ponder;

import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.blocks.connector.base.ConnectorMode;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.util.CALang;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


public class PonderScenes {
	public static void electricMotor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("electric_motor", CALang.translateDirect("ponder.electric_motor.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos motor = util.grid().at(3, 1, 2);

		for (int i = 0; i < 3; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(1 + i, 1, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.effects().rotationDirectionIndicator(motor);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.electric_motor.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(motor));
		scene.idle(50);


		scene.rotateCameraY(90);
		scene.idle(20);

		Vec3 blockSurface = util.vector().blockSurface(motor, Direction.EAST);
		AABB point = new AABB(blockSurface, blockSurface);
		AABB expanded = point.inflate(1 / 16f, 1 / 5f, 1 / 5f);

		scene.overlay().chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, point, 1);
		scene.idle(1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.WHITE, blockSurface, expanded, 60);
		scene.overlay().showControls(blockSurface, Pointing.DOWN, 60).scroll();
		scene.idle(20);

		scene.addKeyframe();
		scene.overlay().showText(70)
			.text(CALang.translateDirect("ponder.electric_motor.text_2").getString())
			.placeNearTarget()
			.pointAt(blockSurface);
		scene.idle(10);
		scene.world().modifyKineticSpeed(util.select().fromTo(1, 1, 2, 3, 1, 2), f -> (4 * f));
		scene.effects().rotationSpeedIndicator(motor);
		scene.idle(70);

		scene.addKeyframe();
		scene.overlay().showText(70)
		.text(CALang.translateDirect("ponder.electric_motor.text_3").getString())
		.placeNearTarget()
		.pointAt(blockSurface);
		scene.idle(80);

		scene.overlay().showText(70)
		.text(CALang.translateDirect("ponder.electric_motor.text_4").getString())
		.placeNearTarget()
		.pointAt(blockSurface);
		scene.idle(80);
		scene.markAsFinished();


		scene.rotateCameraY(-90);
	}

	public static void alternator(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("alternator", CALang.translateDirect("ponder.alternator.header").getString());
		scene.configureBasePlate(1, 0, 4);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos generator = util.grid().at(3, 1, 2);

		for (int i = 0; i < 6; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(i, 1, 2), Direction.DOWN);
			//scene.world().showSection(util.select().position(i, 2, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.alternator.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(generator));
		scene.idle(60);

		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.alternator.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(generator));
		scene.idle(60);


		scene.overlay().showText(50)
		.text(CALang.translateDirect("ponder.alternator.text_3").getString())
		.placeNearTarget()
		.pointAt(util.vector().topOf(generator));
		scene.idle(60);
		scene.markAsFinished();
	}

	public static void rollingMill(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("rolling_mill", CALang.translateDirect("ponder.rolling_mill.header").getString());
		scene.configureBasePlate(1, 0, 4);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos mill = util.grid().at(3, 1, 2);

		for (int i = 0; i < 6; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(i, 1, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.rolling_mill.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(mill));
		scene.idle(60);

		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.rolling_mill.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(mill));
		scene.idle(60);

		scene.addKeyframe();
		scene.overlay().showControls(util.vector().topOf(mill), Pointing.DOWN, 50).rightClick();
		scene.overlay().showText(50)
		.text(CALang.translateDirect("ponder.rolling_mill.text_3").getString())
		.placeNearTarget()
		.pointAt(util.vector().topOf(mill));
		scene.idle(60);
		scene.markAsFinished();
	}

	public static void automateRollingMill(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("automated_rolling_mill", CALang.translateDirect("ponder.automated_rolling_mill.header").getString());
		scene.configureBasePlate(1, 0, 4);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos mill = util.grid().at(3, 2, 2);
		BlockPos in = util.grid().at(3, 2, 3);
		BlockPos out = util.grid().at(3, 2, 1);

		//BlockPos entryBeltPos = util.grid().at(3, 1, 4);
		//BlockPos exitBeltPos = util.grid().at(3, 1, 0);

		for (int i = 0; i < 3; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(i, 1, 4), Direction.DOWN);
		}

		for (int i = 5; i >= 0; i--) {
			scene.idle(5);
			scene.world().showSection(util.select().position(3, 1, i), Direction.DOWN);
			//scene.world().showSection(util.select().position(3, 2, i), Direction.DOWN);
			scene.world().showSection(util.select().position(4, 1, i), Direction.DOWN);
			scene.world().showSection(util.select().position(4, 2, i), Direction.DOWN);
		}

		scene.world().showSection(util.select().position(mill), Direction.DOWN);

		scene.addKeyframe();
		scene.overlay().showText(50)
		.text(CALang.translateDirect("ponder.automated_rolling_mill.text_1").getString())
		.placeNearTarget()
		.pointAt(util.vector().topOf(mill));
		scene.idle(60);

		scene.idle(5);
		scene.world().showSection(util.select().position(in), Direction.NORTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(out), Direction.SOUTH);
		scene.idle(20);
		scene.markAsFinished();
	}

	public static void ccMotor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("cc_electric_motor", CALang.translateDirect("ponder.cc_electric_motor.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos motor = util.grid().at(2, 1, 2);
		BlockPos computer = util.grid().at(1, 1, 2);

		scene.idle(5);
		scene.world().showSection(util.select().position(motor), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(computer), Direction.DOWN);

		scene.idle(10);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.cc_electric_motor.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(computer));
		scene.idle(60);

		scene.idle(10);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.cc_electric_motor.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(computer));
		scene.idle(60);

		scene.addKeyframe();
		scene.idle(10);
		scene.overlay().showText(150)
			.text(CALang.translateDirect("ponder.cc_electric_motor.text_3").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(computer));
		scene.idle(160);
		scene.markAsFinished();
	}

	public static void teslaCoil(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("tesla_coil", CALang.translateDirect("ponder.tesla_coil.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		scene.world().setBlock(util.grid().at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos depotPos = util.grid().at(2, 1, 2);
		scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(2, 3, 2), Direction.DOWN);
		scene.idle(5);
		Vec3 topOf = util.vector().topOf(depotPos);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.tesla_coil.text_1").getString())
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(60);

		scene.world().createItemOnBeltLike(depotPos, Direction.NORTH, AllItems.CHROMATIC_COMPOUND.asStack());
		scene.idle(10);
		scene.world().setBlock(util.grid().at(2, 3, 2), CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoilBlock.FACING, Direction.UP).setValue(TeslaCoilBlock.POWERED, true), false);
		scene.overlay().showText(70)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.tesla_coil.text_2").getString())
			.placeNearTarget()
			.pointAt(topOf);
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void teslaCoilHurt(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("tesla_coil_hurt", CALang.translateDirect("ponder.tesla_coil_hurt.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		//scene.world().setBlock(util.grid().at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos teslacoil = util.grid().at(2, 1, 2);
		BlockPos lever = util.grid().at(2, 1, 1);
		scene.world().showSection(util.select().position(teslacoil), Direction.DOWN);
		scene.idle(5);
		scene.overlay().showText(70)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.tesla_coil_hurt.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(teslacoil));
		scene.idle(80);
		scene.world().showSection(util.select().position(lever), Direction.SOUTH);

		scene.idle(5);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.tesla_coil_hurt.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().centerOf(lever));
		scene.idle(60);
		scene.world().setBlock(lever, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.POWERED, true).setValue(LeverBlock.FACING, Direction.SOUTH).setValue(LeverBlock.FACE, AttachFace.FLOOR), false);
		scene.idle(5);
		scene.world().setBlock(teslacoil, CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoilBlock.FACING, Direction.DOWN).setValue(TeslaCoilBlock.POWERED, true), false);
		scene.idle(5);
		scene.overlay().showText(70)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.tesla_coil_hurt.text_3").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(teslacoil));
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void liquidBlazeBurner(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("liquid_blaze_burner", CALang.translateDirect("ponder.liquid_blaze_burner.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.idle(5);
		//scene.world().setBlock(util.grid().at(3, 2, 2), Blocks.WATER.defaultBlockState(), false);

		BlockPos burner = util.grid().at(2, 1, 2);
		BlockPos[] blocks = {
				util.grid().at(1, 1, 2),
				util.grid().at(0, 1, 2),
				util.grid().at(0, 2, 2),
				util.grid().at(0, 3, 2)
		};
		scene.world().showSection(util.select().position(burner), Direction.DOWN);
		scene.idle(5);
		scene.overlay().showText(50)
		.attachKeyFrame()
		.text(CALang.translateDirect("ponder.liquid_blaze_burner.text_1").getString())
		.placeNearTarget()
		.pointAt(util.vector().topOf(burner));
		scene.idle(10);
		scene.overlay().showControls(util.vector().topOf(burner), Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(CAItems.STRAW.get()));
		scene.world().setBlock(burner, CABlocks.LIQUID_BLAZE_BURNER.getDefaultState().setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING), false);
		scene.idle(60);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.liquid_blaze_burner.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(burner));
		scene.idle(10);
		scene.overlay().showControls(util.vector().topOf(burner), Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(CAFluids.BIOETHANOL.getBucket().get()));
		scene.idle(60);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.liquid_blaze_burner.text_3").getString())
			.placeNearTarget()
			.pointAt(util.vector().topOf(burner));
		scene.idle(10);

		for (int i = 0; i < blocks.length; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(blocks[i]), Direction.EAST);
		}
		scene.idle(20);
		scene.markAsFinished();
	}


	public static void modularAccumulator(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("modular_accumulator", CALang.translateDirect("ponder.modular_accumulator.header").getString());
		scene.configureBasePlate(0, 0, 4);
		scene.showBasePlate();
		scene.idle(15);

		BlockPos cIn = new BlockPos(1, 3, 1);
		BlockPos cOut = new BlockPos(2, 3, 2);

		var accumulator = util.select().fromTo(1, 1, 1, 2, 2, 2);
		//scene.world().showSection(accumulator, Direction.EAST);
		ElementLink<WorldSectionElement> accumulatorLink = scene.world().showIndependentSection(accumulator, Direction.EAST);
		scene.idle(15);
		scene.overlay().showOutline(PonderPalette.GREEN, accumulatorLink, accumulator, 50);

		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.modular_accumulator.text_1").getString())
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cIn));
		scene.idle(60);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.modular_accumulator.text_2").getString())
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cIn));
		scene.idle(60);
		scene.world().showSection(util.select().position(cIn), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(cOut), Direction.DOWN);

		scene.idle(15);
		scene.overlay().showControls(util.vector().centerOf(cIn), Pointing.DOWN, 0).rightClick()
				.withItem(new ItemStack(AllItems.WRENCH.get()));
		scene.world().setBlock(cIn, CABlocks.SMALL_CONNECTOR.getDefaultState().setValue(AbstractConnectorBlock.FACING, Direction.DOWN).setValue(AbstractConnectorBlock.MODE, ConnectorMode.Push), false);
		scene.overlay().showText(50)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.modular_accumulator.text_3").getString())
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cIn));

		scene.idle(60);
		scene.overlay().showControls(util.vector().centerOf(cOut), Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(AllItems.WRENCH.get()));
		scene.world().setBlock(cOut, CABlocks.SMALL_CONNECTOR.getDefaultState().setValue(AbstractConnectorBlock.FACING, Direction.DOWN).setValue(AbstractConnectorBlock.MODE, ConnectorMode.Pull), false);
		scene.overlay().showText(50)
			.text(CALang.translateDirect("ponder.modular_accumulator.text_4").getString())
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cOut));
		scene.idle(60);

		scene.overlay().showText(110)
		.text(CALang.translateDirect("ponder.modular_accumulator.text_5").getString())
		.placeNearTarget()
		.pointAt(util.vector().centerOf(cOut));
		scene.idle(120);
		scene.markAsFinished();
	}

	public static void peiTransfer(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("pei_transfer", CALang.translateDirect("ponder.pei_transfer.header").getString());
		scene.configureBasePlate(0, 0, 6);
		scene.scaleSceneView(0.95f);
		scene.setSceneOffsetY(-1);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);

		BlockPos bearing = util.grid().at(5, 1, 2);
		scene.world().showSection(util.select().position(bearing), Direction.DOWN);
		scene.idle(5);
		ElementLink<WorldSectionElement> contraption =
			scene.world().showIndependentSection(util.select().fromTo(5, 2, 2, 6, 4, 2), Direction.DOWN);
		scene.world().configureCenterOfRotation(contraption, util.vector().centerOf(bearing));
		scene.idle(10);
		scene.world().rotateBearing(bearing, 360, 70);
		scene.world().rotateSection(contraption, 0, 360, 0, 70);
		scene.overlay().showText(60)
			.pointAt(util.vector().topOf(bearing.above(2)))
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.pei_transfer.text_1").getString());

		scene.idle(70);
		BlockPos pei = util.grid().at(4, 2, 2);
		scene.world().showSectionAndMerge(util.select().position(pei), Direction.EAST, contraption);
		scene.idle(13);
		scene.effects().superGlue(pei, Direction.EAST, true);

		scene.overlay().showText(80)
			.pointAt(util.vector().topOf(pei))
			.colored(PonderPalette.GREEN)
			.placeNearTarget()
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.pei_transfer.text_2").getString());
		scene.idle(90);

		BlockPos pei2 = pei.west(2);
		scene.world().showSection(util.select().position(pei2), Direction.DOWN);
		scene.overlay().showOutlineWithText(util.select().position(pei.west()), 50)
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.pei_transfer.text_3").getString());
		scene.idle(55);

		scene.world().rotateBearing(bearing, 360, 60);
		scene.world().rotateSection(contraption, 0, 360, 0, 60);
		scene.idle(20);

		scene.overlay().showText(40)
			.placeNearTarget()
			.pointAt(util.vector().of(3, 3, 2.5))
			.text(CALang.translateDirect("ponder.pei_transfer.text_4").getString());
		scene.idle(35);

		Selection both = util.select().fromTo(2, 2, 2, 4, 2, 2);
		Class<PortableEnergyInterfaceBlockEntity> peiClass = PortableEnergyInterfaceBlockEntity.class;

		scene.world().modifyBlockEntityNBT(both, peiClass, nbt -> {
			nbt.putFloat("Distance", 1);
			nbt.putFloat("Timer", 40);
		});

		scene.idle(20);
		scene.overlay().showOutline(PonderPalette.GREEN, pei, util.select().fromTo(5, 3, 2, 6, 4, 2), 80);
		scene.idle(10);

		scene.overlay().showOutlineWithText(util.select().position(pei2), 70)
			.placeNearTarget()
			.colored(PonderPalette.GREEN)
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.pei_transfer.text_5").getString());

		scene.idle(80);

		BlockPos connector = util.grid().at(2, 3, 2);
		scene.world().showSection(util.select().position(connector), Direction.DOWN);
		scene.overlay().showText(70)
			.placeNearTarget()
			.pointAt(util.vector().centerOf(connector))
			.attachKeyFrame()
			.text(CALang.translateDirect("ponder.pei_transfer.text_6").getString());
		scene.idle(80);

		scene.overlay().showText(120)
			.placeNearTarget()
			.pointAt(util.vector().centerOf(pei2))
			.text(CALang.translateDirect("ponder.pei_transfer.text_7").getString());
		scene.world().modifyBlockEntityNBT(both, peiClass, nbt -> nbt.putFloat("Timer", 9));

		scene.idle(15);
		scene.world().rotateBearing(bearing, 270, 120);
		scene.world().rotateSection(contraption, 0, 270, 0, 120);
		scene.markAsFinished();
	}

	public static void peiRedstone(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("pei_redstone", CALang.translateDirect("ponder.pei_redstone.header").getString());
		scene.configureBasePlate(0, 0, 5);
		scene.setSceneOffsetY(-1);

		Class<PortableEnergyInterfaceBlockEntity> peiClass = PortableEnergyInterfaceBlockEntity.class;
		Selection peis = util.select().fromTo(1, 1, 3, 1, 3, 3);
		scene.world().modifyBlockEntityNBT(peis, peiClass, nbt -> {
			nbt.putFloat("Distance", 1);
			nbt.putFloat("Timer", 40);
		});

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);
		scene.world().showSection(util.select().layer(1), Direction.DOWN);
		scene.idle(5);

		ElementLink<WorldSectionElement> contraption =
			scene.world().showIndependentSection(util.select().layersFrom(2), Direction.DOWN);
		BlockPos bearing = util.grid().at(3, 1, 3);
		scene.world().configureCenterOfRotation(contraption, util.vector().topOf(bearing));
		scene.idle(20);
		scene.world().modifyBlockEntityNBT(peis, peiClass, nbt -> nbt.putFloat("Timer", 9));
		scene.idle(20);
		scene.world().rotateBearing(bearing, 360 * 3 + 270, 240 + 60);
		scene.world().rotateSection(contraption, 0, 360 * 3 + 270, 0, 240 + 60);
		scene.idle(20);

		scene.world().toggleRedstonePower(util.select().fromTo(1, 1, 1, 1, 1, 2));
		scene.effects().indicateRedstone(util.grid().at(1, 1, 1));

		scene.idle(10);

		scene.overlay().showOutlineWithText(util.select().position(1, 1, 3), 120)
			.colored(PonderPalette.RED)
			.text(CALang.translateDirect("ponder.pei_redstone.text_1").getString());

		scene.idle(20);
		scene.markAsFinished();
	}
}
