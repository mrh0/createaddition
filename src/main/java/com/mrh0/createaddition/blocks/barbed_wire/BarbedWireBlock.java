package com.mrh0.createaddition.blocks.barbed_wire;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.CADamageTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.IShearable;

public class BarbedWireBlock extends Block implements IShearable, SimpleWaterloggedBlock, IWrenchable {
	public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public BarbedWireBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, false).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !(Boolean)state.getValue(WATERLOGGED);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		double delta = Math.abs(entity.getX() - entity.xOld) + Math.abs(entity.getY() - entity.yOld) + Math.abs(entity.getZ() - entity.zOld);
		if((entity instanceof LivingEntity) && delta > 0d) {
			if(entity.hurt(CADamageTypes.barbedWire(level), CommonConfig.BARBED_WIRE_DAMAGE.get().floatValue()))
				entity.playSound(SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, 1f, 1f);
		}
		entity.makeStuckInBlock(state, new Vec3(0.25D, 0.05D, 0.25D));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(VERTICAL, HORIZONTAL_FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		FluidState fluidstate = c.getLevel().getFluidState(c.getClickedPos());
		if(c.getPlayer() == null) return defaultBlockState();
		if(c.getClickedFace().getAxis() == Axis.Y)
			return defaultBlockState()
					.setValue(HORIZONTAL_FACING, c.getPlayer().isShiftKeyDown() ? c.getHorizontalDirection().getClockWise() : c.getHorizontalDirection())
					.setValue(VERTICAL, false)
					.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
		else
			return defaultBlockState()
					.setValue(HORIZONTAL_FACING, c.getClickedFace().getOpposite())
					.setValue(VERTICAL, true)
					.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

    public static void makeBlockState(DataGenContext<Block, BarbedWireBlock> ctx, RegistrateBlockstateProvider provider) {
		BlockModelProvider models = provider.models();
		String basePath = "block/barbed_wire/block";
		ModelFile.ExistingModelFile model = models.getExistingFile(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, basePath));
		VariantBlockStateBuilder builder = provider.getVariantBuilder(ctx.get());
		builder.forAllStates(state -> ConfiguredModel.builder()
				.modelFile(model)
				.rotationX(state.getValue(VERTICAL) ? 90 : 0)
				.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + (state.getValue(VERTICAL) ? 0: 90)) % 360)
				.build()
		);
	}
}
