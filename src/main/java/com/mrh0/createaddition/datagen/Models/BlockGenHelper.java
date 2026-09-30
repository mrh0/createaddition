package com.mrh0.createaddition.datagen.Models;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.util.TransformationHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public class BlockGenHelper {
    public static NonNullBiConsumer<DataGenContext<Block, ElectricMotorBlock>, RegistrateBlockstateProvider> directionalBlockState(ResourceLocation resourceLocation) {
        return (ctx, prov) -> directionalModel(resourceLocation, ctx, prov);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockState() {
        return (ctx, prov) -> directionalModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov);
    }

    public static NonNullBiConsumer<DataGenContext<Block, ElectricMotorBlock>, RegistrateBlockstateProvider> horizontalBlockState(ResourceLocation resourceLocation) {
        return (ctx, prov) -> horizontalModel(resourceLocation, ctx, prov, 0);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalBlockState() {
        return (ctx, prov) -> horizontalModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov, 90);
    }

    public static <T extends Block> void directionalModel(ResourceLocation resourceLocation, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        prov.getVariantBuilder(ctx.get())
                .forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationX(dir == Direction.DOWN ? 270
                            : dir.getAxis()
                            .isHorizontal() ? 0 : 90)
                    .rotationY(dir.getAxis()
                            .isVertical() ? 90 : (((int) dir.toYRot()) + 360) % 360)
                    .build();
        }, BlockStateProperties.WATERLOGGED, BlockStateProperties.POWERED);
    }

    /**
     * Like {@link #directionalBlockState()}, but also rotates the model around FACING so that the
     * model's up side points towards {@code top}. The model must face south with its top side up.
     */
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> rollableDirectionalBlockState(Function<BlockState, Direction> top) {
        return (ctx, prov) -> rollableDirectionalModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), top, ctx, prov);
    }

    public static <T extends Block> void rollableDirectionalModel(ResourceLocation resourceLocation, Function<BlockState, Direction> topGetter, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        // Blockstate x/y rotations can't lay a horizontally facing model on its side, so those
        // orientations use a copy that is pre-rolled 90° around the model's facing (Z) axis.
        ModelFile rolledModel = models.withExistingParent(resourceLocation.getPath() + "_rolled", resourceLocation)
                .rootTransforms()
                .rotation(0, 0, 90, true)
                .origin(TransformationHelper.TransformOrigin.CENTER)
                .end();
        prov.getVariantBuilder(ctx.get())
                .forAllStatesExcept(state -> {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction top = topGetter.apply(state);
            for (int roll = 0; roll <= 90; roll += 90)
                for (int x = 0; x < 360; x += 90)
                    for (int y = 0; y < 360; y += 90) {
                        // Same composition as BlockModelRotation applied after the NeoForge root transform.
                        Quaternionf rotation = new Quaternionf()
                                .rotateYXZ((float) Math.toRadians(-y), (float) Math.toRadians(-x), 0)
                                .rotateZ((float) Math.toRadians(roll));
                        if (rotateDirection(rotation, Direction.SOUTH) == facing && rotateDirection(rotation, Direction.UP) == top)
                            return ConfiguredModel.builder()
                                    .modelFile(roll == 0 ? blockModel : rolledModel)
                                    .rotationX(x)
                                    .rotationY(y)
                                    .build();
                    }
            throw new IllegalStateException("No model rotation for facing " + facing + " with top " + top);
        }, BlockStateProperties.WATERLOGGED, BlockStateProperties.POWERED);
    }

    private static Direction rotateDirection(Quaternionf rotation, Direction direction) {
        Vector3f v = rotation.transform(new Vector3f(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
        return Direction.getNearest(v.x, v.y, v.z);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlock(ResourceLocation resourceLocation) {
        return (ctx, prov) -> simpleBlockModel(resourceLocation, ctx, prov);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlock() {
        return (ctx, prov) -> simpleBlockModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov);
    }

    public static <T extends Block>  void horizontalModel(ResourceLocation resourceLocation, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, int angleOffset) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        prov.getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(blockModel)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360).build()
                );
    }

    public static <T extends Block> void simpleBlockModel(ResourceLocation resourceLocation, DataGenContext<Block,T> ctx, RegistrateBlockstateProvider prov) {
        ModelFile.ExistingModelFile blockModel = prov.models().getExistingFile(resourceLocation);
        prov.simpleBlock(ctx.get(),blockModel);
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> pumpBlockState() {
        return (ctx, prov) -> pumpModel(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "block/" + getBlockName(ctx.get()) + "/block"), ctx, prov);
    }

    public static <T extends Block> void pumpModel(ResourceLocation resourceLocation, DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockModelProvider models = prov.models();
        ModelFile.ExistingModelFile blockModel = models.getExistingFile(resourceLocation);
        prov.getVariantBuilder(ctx.get())
                .forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            int rotX;
            int rotY = 0;
            switch (dir) {
                case DOWN -> rotX = 180;
                case NORTH -> rotX = 90;
                case SOUTH -> { rotX = 90; rotY = 180; }
                case WEST -> { rotX = 90; rotY = 270; }
                case EAST -> { rotX = 90; rotY = 90; }
                default -> rotX = 0; // UP
            }
            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        }, BlockStateProperties.WATERLOGGED, BlockStateProperties.POWERED);
    }

    public static String getBlockName(Block block) {
        return  BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
