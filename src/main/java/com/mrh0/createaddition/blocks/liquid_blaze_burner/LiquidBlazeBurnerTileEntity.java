package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import com.mrh0.createaddition.util.liquid_burning.FluidTagRecipeComparator;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@SuppressWarnings({"UnstableApiUsage"})
public class LiquidBlazeBurnerTileEntity extends SmartTileEntity implements IHaveGoggleInformation, IObserveTileEntity, SidedStorageBlockEntity {
	public static final int MAX_HEAT_CAPACITY;
	protected FuelType activeFuel;
	protected int remainingBurnTime;
	protected LerpedFloat headAnimation;
	protected LerpedFloat headAngle;
	protected boolean isCreative;
	protected boolean goggles;
	protected boolean hat;
	protected FluidTank fluidTank;

	static {
		MAX_HEAT_CAPACITY = 10000;
	}

	public LiquidBlazeBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		activeFuel = FuelType.NONE;
		remainingBurnTime = 0;
		headAnimation = LerpedFloat.linear();
		headAngle = LerpedFloat.angular();
		isCreative = false;
		goggles = false;

		headAngle.startWithValue((AngleHelper.horizontalAngle(state.getOptionalValue(LiquidBlazeBurner.FACING)
				.orElse(Direction.SOUTH)) + 180) % 360);

		fluidTank = new FluidTank(324000);
	}

	@Override
	public void tick() {
		super.tick();

		assert level != null;
		if (level.isClientSide) {
			tickAnimation();
			if (!isVirtual())
				spawnParticles(getHeatLevelFromBlock());
			return;
		}

		burningTick();

		if (isCreative)
			return;

		FluidTagRecipeComparator.argsToTag(fluidTank.getFluid().getFluid(), (tagProperties, tagKey) ->
				setBurnTag(
					tagKey,
					tagProperties.asResource(),
					tagProperties.getSuperheated(),
					tagProperties.getTime() / 100,
					tagProperties.getDropletAmount() / 100
				)
		);

		if (remainingBurnTime > 0)
			remainingBurnTime--;

		if (activeFuel == FuelType.NORMAL || activeFuel == FuelType.SPECIAL)
			updateBlockState();

		if (remainingBurnTime > 0)
			return;

		if (activeFuel == FuelType.SPECIAL) {
			activeFuel = FuelType.NORMAL;
			remainingBurnTime = MAX_HEAT_CAPACITY / 2;
		} else
			activeFuel = FuelType.NONE;

		updateBlockState();
	}
	private boolean setBurnTag(TagKey<Fluid> tagKey, ResourceLocation burnableTagLocation, boolean superheated, int time, int fluidDropletAmount) {
		if (
						tagKey.location().equals(burnableTagLocation) &&
						!Transaction.isOpen() &&
						fluidTank.getFluidAmount() >= fluidDropletAmount &&
						remainingBurnTime <= 200
		) {
			Transaction transaction = Transaction.openOuter();
			fluidTank.extract(fluidTank.variant, fluidDropletAmount, transaction);
			transaction.commit();
			remainingBurnTime = remainingBurnTime + time;
			activeFuel = superheated ? FuelType.SPECIAL : FuelType.NORMAL;
			return true;
		}
		return false;
	}

	public void burningTick() {
		assert level != null;
		if (level.isClientSide())
			return;
		if (remainingBurnTime < 1)
			return;
		if (remainingBurnTime > MAX_HEAT_CAPACITY)
			return;

		if (getHeatLevelFromBlock() != getHeatLevelFromBlock()) {
			level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
					.125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

			spawnParticleBurst(activeFuel == FuelType.SPECIAL);
		}

	}

	@Environment(EnvType.CLIENT)
	private void tickAnimation() {
		boolean active = getHeatLevelFromBlock().isAtLeast(HeatLevel.FADING) && isValidBlockAbove();

		if (!active) {
			float target = 0;
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null && !player.isInvisible()) {
				double x;
				double z;
				if (isVirtual()) {
					x = -4;
					z = -10;
				} else {
					x = player.getX();
					z = player.getZ();
				}
				double dx = x - (getBlockPos().getX() + 0.5);
				double dz = z - (getBlockPos().getZ() + 0.5);
				target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
			}
			target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
			headAngle.chase(target, .25f, Chaser.exp(5));
			headAngle.tickChaser();
		} else {
			headAngle.chase((AngleHelper.horizontalAngle(getBlockState().getOptionalValue(LiquidBlazeBurner.FACING)
					.orElse(Direction.SOUTH)) + 180) % 360, .125f, Chaser.EXP);
			headAngle.tickChaser();
		}

		headAnimation.chase(active ? 1 : 0, .25f, Chaser.exp(.25f));
		headAnimation.tickChaser();
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		if (!isCreative) {
			compound.putInt("fuelLevel", activeFuel.ordinal());
			compound.putInt("burnTimeRemaining", remainingBurnTime);
		} else
			compound.putBoolean("isCreative", true);
		if (goggles)
			compound.putBoolean("Goggles", true);
		if (hat)
			compound.putBoolean("TrainHat", true);
		compound.put("TankContent", fluidTank.writeToNBT(new CompoundTag()));
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		activeFuel = FuelType.values()[compound.getInt("fuelLevel")];
		remainingBurnTime = compound.getInt("burnTimeRemaining");
		isCreative = compound.getBoolean("isCreative");
		goggles = compound.contains("Goggles");
		hat = compound.contains("TrainHat");
		fluidTank.readFromNBT(compound.getCompound("TankContent"));
		super.read(compound, clientPacket);
	}

	public HeatLevel getHeatLevelFromBlock() {
		return BlazeBurnerBlock.getHeatLevelOf(getBlockState());
	}

	public void updateBlockState() {
		setBlockHeat(getHeatLevelFromFuelType());
	}

	protected void setBlockHeat(HeatLevel heat) {
		HeatLevel inBlockState = getHeatLevelFromBlock();
		if (inBlockState == heat)
			return;
		assert level != null;
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(LiquidBlazeBurner.HEAT_LEVEL, heat));
		notifyUpdate();
	}

	private boolean tryUpdateLiquid(ItemStack itemStack, Player player, InteractionHand hand) {
		if (fluidTank.getFluidAmount() + 81000 > fluidTank.getCapacity()) {
			return false;
		}

		Storage<FluidVariant> itemsFluidVariantStorage = FluidStorage.ITEM.find(
				itemStack,
				ContainerItemContext.forPlayerInteraction(player, hand)
			);

		if (itemsFluidVariantStorage == null) {
			return false;
		}
		if (level == null) {
			return false;
		}

		for (StorageView<FluidVariant> view : itemsFluidVariantStorage) {
			Fluid itemsFluid = view.getResource().getFluid();
			if (FluidTagRecipeComparator.argsToTag(itemsFluid, (tagProperties, tagKey) -> {

				if (	tagKey.location().equals(tagProperties.asResource())
						&& tagProperties.getTime() >= 100
						&& (fluidTank.getAmount() == 0 || fluidTank.getFluid().getFluid() == itemsFluid)
				) {
					fluidTank.setFluid(new FluidStack(itemsFluid, fluidTank.getAmount() + tagProperties.getDropletAmount()));

					level.playSound(player, getBlockPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, .125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
					if (level.isClientSide) {
						spawnParticleBurst(activeFuel == FuelType.SPECIAL);
					}
					playSound();
					level.playSound(player, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
							.125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

					return true;
				}
				return null;
			})) {
				return true;
			}
		}
		return  false;
	}

	/**
	 * @return true if the heater updated its burn time and an item should be
	 *         consumed
	 */
	protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate, Player player, InteractionHand hand) {
		if (isCreative)
			return false;

		FuelType newFuel = FuelType.NONE;
		Integer newBurnTime;

		if (tryUpdateLiquid(itemStack, player, hand))
			return true;

		Storage<FluidVariant> itemsFluidVariantStorage = FluidStorage.ITEM.find(
				itemStack,
				ContainerItemContext.forPlayerInteraction(player, hand)
		);

			if (itemsFluidVariantStorage == null) {
			return false;
		}

		for (StorageView<FluidVariant> view : itemsFluidVariantStorage) {
			Fluid fluid = view.getResource().getFluid();
			if (fluid == Fluids.FLOWING_LAVA || fluid == Fluids.LAVA) {
				return false;
			}
		}

		if (AllTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.matches(itemStack)) {
			newBurnTime = 1000;
			newFuel = FuelType.SPECIAL;
		} else {
			FuelRegistry registry = FuelRegistry.INSTANCE;
			newBurnTime = registry.get(itemStack.getItem());
			if (newBurnTime == null) newBurnTime = 0;
			if (newBurnTime > 0)
				newFuel = FuelType.NORMAL;
			else if (AllTags.AllItemTags.BLAZE_BURNER_FUEL_REGULAR.matches(itemStack)) {
				newBurnTime = 1600; // Same as coal
				newFuel = FuelType.NORMAL;
			}
		}

		if (newFuel == FuelType.NONE)
			return false;
		if (newFuel.ordinal() < activeFuel.ordinal())
			return false;
		if (activeFuel == FuelType.SPECIAL && remainingBurnTime > 20)
			return false;

		if (newFuel == activeFuel) {
			if (remainingBurnTime + newBurnTime > MAX_HEAT_CAPACITY && !forceOverflow)
				return false;
			newBurnTime = Mth.clamp(remainingBurnTime + newBurnTime, 0, MAX_HEAT_CAPACITY);
		}

		if (simulate)
			return true;

		activeFuel = newFuel;
		remainingBurnTime = newBurnTime;

		assert level != null;
		if (level.isClientSide) {
			spawnParticleBurst(activeFuel == FuelType.SPECIAL);
			return true;
		}

		HeatLevel prev = getHeatLevelFromBlock();
		playSound();
		updateBlockState();

		if (prev != getHeatLevelFromBlock())
			level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
				.125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

		return true;
	}

	public boolean isValidBlockAbove() {
		assert level != null;
		BlockState blockState = level.getBlockState(worldPosition.above());
		return AllBlocks.BASIN.has(blockState) || blockState.getBlock() instanceof FluidTankBlock;
	}

	protected void playSound() {
		assert level != null;
		level.playSound(null, worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS,
			.125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
	}

	protected HeatLevel getHeatLevelFromFuelType() {
		HeatLevel level = HeatLevel.SMOULDERING;
		switch (activeFuel) {
		case SPECIAL:
			level = HeatLevel.SEETHING;
			break;
		case NORMAL:
			boolean lowPercent = (double) remainingBurnTime / MAX_HEAT_CAPACITY < 0.0125;
			level = lowPercent ? HeatLevel.FADING : HeatLevel.KINDLED;
			break;
		default:
		case NONE:
			break;
		}
		return level;
	}

	protected void spawnParticles(HeatLevel heatLevel) {
		if (level == null)
			return;
		if (heatLevel == HeatLevel.NONE)
			return;

		RandomSource r = level.getRandom();

		Vec3 c = VecHelper.getCenterOf(worldPosition);
		Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
			.multiply(1, 0, 1));

		if (r.nextInt(3) == 0)
			level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);
		if (r.nextInt(2) != 0)
			return;

		boolean empty = level.getBlockState(worldPosition.above())
			.getCollisionShape(level, worldPosition.above())
			.isEmpty();

		double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
		Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
			.multiply(1, .25f, 1)
			.normalize()
			.scale((empty ? .25f : .5) + r.nextDouble() * .125f))
			.add(0, .5, 0);

		if (heatLevel.isAtLeast(HeatLevel.SEETHING)) {
			level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
		} else if (heatLevel.isAtLeast(HeatLevel.FADING)) {
			level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
		}
	}

	public void spawnParticleBurst(boolean soulFlame) {
		Vec3 c = VecHelper.getCenterOf(worldPosition);
		assert level != null;
		RandomSource r = level.random;
		for (int i = 0; i < 20; i++) {
			Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
				.multiply(1, .25f, 1)
				.normalize();
			Vec3 v = c.add(offset.scale(.5 + r.nextDouble() * .125f))
				.add(0, .125, 0);
			Vec3 m = offset.scale(1 / 32f);

			level.addParticle(soulFlame ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, v.x, v.y, v.z, m.x, m.y,
				m.z);
		}
	}
	@Override
	public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction direction) {
		return fluidTank;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ObservePacket.send(worldPosition, 0);
		return containedFluidTooltip(tooltip, isPlayerSneaking, fluidTank);
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		causeBlockUpdate();
	}

	public enum FuelType {
		NONE, NORMAL, SPECIAL
	}
}
