package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.network.IObserveBlockEntity;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.mrh0.createaddition.network.TimeRemainingPacketPayload;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;

public class LiquidBlazeBurnerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IObserveBlockEntity {
	public int MAX_HEAT_CAPACITY = CommonConfig.LIQUID_BLAZE_BURNER_MAX_HEAT_CAPACITY.get();

	protected FuelType activeFuel;
	protected int remainingBurnTime;
	protected LerpedFloat headAnimation;
	protected LerpedFloat headAngle;
	protected boolean isCreative;
	protected boolean goggles;
	protected boolean hat;
	public final boolean stockKeeper = false;
	BlazeBurnerBlock.HeatLevel heatLevel;
	private ScrollValueBehaviour HEAT_CAPACITY;
	private ScrollValueBehaviour LIQUID_CAPACITY;

	public LiquidBlazeBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		activeFuel = FuelType.NONE;
		remainingBurnTime = 0;
		headAnimation = LerpedFloat.linear();
		headAngle = LerpedFloat.angular();
		isCreative = false;
		goggles = false;

		headAngle.startWithValue((AngleHelper.horizontalAngle(state.getOptionalValue(LiquidBlazeBurnerBlock.FACING)
			.orElse(Direction.SOUTH)) + 180) % 360);

		tankInventory = createInventory();
		heatLevel = getHeatLevelFromBlock();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		LIQUID_CAPACITY = new LiquidBlazeScrollValueBehaviourLiquid(Component.translatable(CreateAddition.MODID + ".tooltip.liquid_burning.liquid_capacity"), this,
			new LiquidBlazeScrollSlot(false)).between(0, defaultLiquidValue());
		LIQUID_CAPACITY.withFormatter(this::formatLiquid);
		LIQUID_CAPACITY.withCallback(v -> syncTankCapacity());
		LIQUID_CAPACITY.setValue(defaultLiquidValue());
		behaviours.add(LIQUID_CAPACITY);

		HEAT_CAPACITY = new LiquidBlazeScrollValueBehaviourHeat(Component.translatable(CreateAddition.MODID + ".tooltip.liquid_burning.heat_capacity"), this,
			new LiquidBlazeScrollSlot(true)).between(0, defaultHeatValue());
		HEAT_CAPACITY.withFormatter(this::formatHeat);
		HEAT_CAPACITY.withCallback(v -> MAX_HEAT_CAPACITY = v);
		HEAT_CAPACITY.setValue(defaultHeatValue());
		behaviours.add(HEAT_CAPACITY);
	}

	protected int defaultHeatValue() {
		return CommonConfig.LIQUID_BLAZE_BURNER_MAX_HEAT_CAPACITY.get();
	}

	protected int defaultLiquidValue() {
		return CommonConfig.LIQUID_BLAZE_BURNER_MAX_LIQUID_CAPACITY.get();
	}

	private void syncTankCapacity(){
		if (tankInventory == null || LIQUID_CAPACITY == null) return;
    	int capacity = LIQUID_CAPACITY.getValue();
    	tankInventory.setCapacity(capacity);
    	if (tankInventory.getFluidAmount() > capacity)
        	tankInventory.drain(tankInventory.getFluidAmount() - capacity, IFluidHandler.FluidAction.EXECUTE);
		notifyUpdate();
	}

	public BlazeBurnerBlock.HeatLevel getHeatLevelForRender() {
		return getHeatLevelFromBlock();
	}

	// Custom fluid handling
	protected FluidTank tankInventory;

	private Optional<RecipeHolder<LiquidBurningRecipe>> recipeCache = Optional.empty();
	private Fluid lastFluid = null;

	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.FluidHandler.BLOCK,
				CABlockEntities.LIQUID_BLAZE_BURNER.get(),
				(be, direction) -> be.tankInventory
		);
	}

	protected SmartFluidTank createInventory() {
		return new SmartFluidTank(CommonConfig.LIQUID_BLAZE_BURNER_MAX_LIQUID_CAPACITY.get(), this::onFluidStackChanged);
	}

	protected void onFluidStackChanged(FluidStack newFluidStack) {
		if (!hasLevel()) return;
		syncTankCapacity();
		update(newFluidStack);
	}

	private void update(FluidStack stack) {
		if (level == null) return;
		if (level.isClientSide()) return;
		if (stack.getFluid() != lastFluid) recipeCache = find(stack, level);
		lastFluid = stack.getFluid();
	}

	public Optional<RecipeHolder<LiquidBurningRecipe>> find(@Nullable FluidStack stack,@Nullable Level level) {
		if (stack == null || level == null) return Optional.empty();
		if (CARecipes.LIQUID_BURNING_TYPE.get() == null) return Optional.empty();
		return level.getRecipeManager().getRecipeFor(CARecipes.LIQUID_BURNING_TYPE.get(), new FluidRecipeWrapper(new FluidStack(stack.getFluid(), 1000)), level);
	}

	public boolean first = true;
	public void burningTick() {
		if (level == null) return;
		if (level.isClientSide()) return;

		if (first) update(tankInventory.getFluid());
		first = false;

		if (remainingBurnTime < 1)
			if (recipeCache.isEmpty()) return;

		if (tankInventory.getFluidAmount() < 100) return;
		if (remainingBurnTime > MAX_HEAT_CAPACITY) return;
		if (recipeCache.isEmpty()) return;

		// Added try catch because this crashes for some reason for a minority of players, very strange.
		try {
			var recipe = recipeCache.get().value();
			var burnTime = recipe.getBurnTime() / 10;
			var fuelType = recipe.isSuperheated() ? FuelType.SPECIAL : FuelType.NORMAL;
			remainingBurnTime = activeFuel == fuelType ? remainingBurnTime + burnTime : burnTime;
			activeFuel = fuelType;
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		tankInventory.drain(100, IFluidHandler.FluidAction.EXECUTE);

		BlazeBurnerBlock.HeatLevel prev = heatLevel;
		playSound();
		updateBlockState();

		if (prev != heatLevel) {
			level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
				.125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

			spawnParticleBurst(activeFuel == FuelType.SPECIAL);
		}
	}


	public FuelType getActiveFuel() {
		return activeFuel;
	}

	public int getRemainingBurnTime() {
		return remainingBurnTime;
	}

	public boolean isCreative() {
		return isCreative;
	}

	private boolean firstTick = true;
	@Override
	public void tick() {
		super.tick();

		if (firstTick) {
			firstTick = false;
			syncTankCapacity();
			if (HEAT_CAPACITY != null) MAX_HEAT_CAPACITY = HEAT_CAPACITY.getValue();
		}

		if (level == null) return;
		if (level.isClientSide) {
			tickAnimation();
			if (!isVirtual()) spawnParticles(getHeatLevelForRender(), 1);
			return;
		}

		burningTick();

		if (isCreative) return;

		if (remainingBurnTime > 0) remainingBurnTime--;

		// if (activeFuel == FuelType.NORMAL) updateBlockState();
		if (remainingBurnTime > 0) return;

		if (activeFuel == FuelType.SPECIAL) {
			activeFuel = FuelType.NORMAL;
			remainingBurnTime = HEAT_CAPACITY.getValue() / 2;
		} else activeFuel = FuelType.NONE;

		updateBlockState();
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
	}

	@OnlyIn(Dist.CLIENT)
    void tickAnimation() {
		var serverHeatLevel = getHeatLevelForRender();
		boolean active = serverHeatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) && isValidBlockAbove();

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
			headAngle.chase(target, .25f, LerpedFloat.Chaser.exp(5));
			headAngle.tickChaser();
		} else {
			headAngle.chase((AngleHelper.horizontalAngle(getBlockState().getOptionalValue(LiquidBlazeBurnerBlock.FACING)
				.orElse(Direction.SOUTH)) + 180) % 360, .125f, LerpedFloat.Chaser.EXP);
			headAngle.tickChaser();
		}

		headAnimation.chase(active ? 1 : 0, .25f, LerpedFloat.Chaser.exp(.25f));
		headAnimation.tickChaser();
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		if (!isCreative) {
			tag.putInt("fuelLevel", activeFuel.ordinal());
			tag.putInt("burnTimeRemaining", remainingBurnTime);
		} else tag.putBoolean("isCreative", true);
		if (goggles) tag.putBoolean("Goggles", true);
		if (hat) tag.putBoolean("TrainHat", true);
		tag.put("TankContent", tankInventory.writeToNBT(registries, new CompoundTag()));
		tag.putInt("TankCapacity", tankInventory.getCapacity());
		super.write(tag, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		activeFuel = FuelType.values()[tag.getInt("fuelLevel")];
		remainingBurnTime = tag.getInt("burnTimeRemaining");
		isCreative = tag.getBoolean("isCreative");
		goggles = tag.contains("Goggles");
		hat = tag.contains("TrainHat");
		tankInventory.readFromNBT(registries, tag.getCompound("TankContent"));
		if (tag.contains("TankCapacity")) tankInventory.setCapacity(tag.getInt("TankCapacity"));
	}

	private String formatHeat(int value) {
		if (value < 60)
			return value + "t";
		if (value < 20 * 60)
			return (value / 20) + "s";
		return (value / 20 / 60) + "m";
	}

	private String formatLiquid(int value) {
		if (value < 1000)
			return value + "mB";
		return (value / 1000) + "B";
	}

	public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() {
		return LiquidBlazeBurnerBlock.getHeatLevelOf(getBlockState());
	}

	public void updateBlockState() {
		setBlockHeat(getHeatLevelFromFuelType(activeFuel));
	}

	protected void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
		if (level == null) return;
		if (heatLevel == heat) return;
		heatLevel = heat;
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HEAT_LEVEL, heat));
		notifyUpdate();
	}

	private boolean tryUpdateLiquid(ItemStack itemStack, boolean simulate) {
		if (level == null) return false;
		var itemHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

		if (itemHandler == null) return false;
		if (itemHandler.getFluidInTank(0).isEmpty()) return false;
		FluidStack stack = itemHandler.getFluidInTank(0);
		Optional<RecipeHolder<LiquidBurningRecipe>> recipe = find(stack, level);
		if (recipe.isEmpty()) return false;

		var beHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
		if (beHandler == null) return false;
		if (beHandler.getTankCapacity(0) - beHandler.getFluidInTank(0).getAmount() < 1000) return false;

		if (!simulate) beHandler.fill(new FluidStack(itemHandler.getFluidInTank(0).getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
		//if (!player.isCreative())
		//	player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
		if (!simulate) level.playSound(null, getBlockPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, .125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
		return true;
	}

	protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate) {
		if (isCreative) return false;

		FuelType newFuel = FuelType.NONE;
		int newBurnTime;

		// Liquid Fluid Logic
		if(tryUpdateLiquid(itemStack, simulate)) return true;

		if (AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.matches(itemStack)) {
			newBurnTime = 3200;
			newFuel = FuelType.SPECIAL;
		} else {
			newBurnTime = itemStack.getBurnTime(null);
			if (newBurnTime > 0)
				newFuel = FuelType.NORMAL;
			else if (AllItemTags.BLAZE_BURNER_FUEL_REGULAR.matches(itemStack)) {
				newBurnTime = 1600; // Same as coal
				newFuel = FuelType.NORMAL;
			}
		}

		if (newFuel == FuelType.NONE) return false;
		if (newFuel.ordinal() < activeFuel.ordinal()) return false;
		if (activeFuel == FuelType.SPECIAL && remainingBurnTime > 20) return false;

		if (newFuel == activeFuel) {
			if (remainingBurnTime + newBurnTime > HEAT_CAPACITY.getValue() && !forceOverflow) return false;
			newBurnTime = Mth.clamp(remainingBurnTime + newBurnTime, 0, HEAT_CAPACITY.getValue());
		}

		if (simulate) return true;

		activeFuel = newFuel;
		remainingBurnTime = newBurnTime;

		if (level == null) return false;
		if (level.isClientSide) {
			spawnParticleBurst(activeFuel == FuelType.SPECIAL);
			return true;
		}

		BlazeBurnerBlock.HeatLevel prev = heatLevel;
		playSound();
		updateBlockState();

		if (prev != heatLevel)
			level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
				.125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);

		return true;
	}

	protected void applyCreativeFuel() {
		activeFuel = FuelType.NONE;
		remainingBurnTime = 0;
		isCreative = true;

		BlazeBurnerBlock.HeatLevel next = heatLevel.nextActiveLevel();

		if (level.isClientSide) {
			spawnParticleBurst(next.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING));
			return;
		}

		playSound();
		if (next == BlazeBurnerBlock.HeatLevel.FADING)
			next = next.nextActiveLevel();
		setBlockHeat(next);
	}

	public boolean isCreativeFuel(ItemStack stack) {
		return AllItems.CREATIVE_BLAZE_CAKE.isIn(stack);
	}

	public boolean isValidBlockAbove() {
		BlockState blockState = level.getBlockState(worldPosition.above());
		return AllBlocks.BASIN.has(blockState) || blockState.getBlock() instanceof FluidTankBlock;
	}

	protected void playSound() {
		level.playSound(null, worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS,
			.125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
	}

	protected BlazeBurnerBlock.HeatLevel getHeatLevelFromFuelType(FuelType fuel) {
		BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.SMOULDERING;
		switch (activeFuel) {
			case SPECIAL:
				level = BlazeBurnerBlock.HeatLevel.SEETHING;
				break;
			case NORMAL:
				boolean lowPercent = (double) remainingBurnTime / HEAT_CAPACITY.getValue() < 0.0125;
				level = lowPercent ? BlazeBurnerBlock.HeatLevel.FADING : BlazeBurnerBlock.HeatLevel.KINDLED;
				break;
			case NONE:
			default:
				break;
		}
		return level;
	}

	protected void spawnParticles(BlazeBurnerBlock.HeatLevel heatLevel, double burstMult) {
		if (level == null) return;
		if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE) return;

		RandomSource r = level.getRandom();

		Vec3 c = VecHelper.getCenterOf(worldPosition);
		Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
			.multiply(1, 0, 1));

		if (r.nextInt(3) == 0) level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);
		if (r.nextInt(2) != 0) return;

		boolean empty = level.getBlockState(worldPosition.above())
			.getCollisionShape(level, worldPosition.above())
			.isEmpty();

		double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
		Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
			.multiply(1, .25f, 1)
			.normalize()
			.scale((empty ? .25f : .5) + r.nextDouble() * .125f))
			.add(0, .5, 0);

		if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
			level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
		} else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
			level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
		}
    }

	public void spawnParticleBurst(boolean soulFlame) {
		Vec3 c = VecHelper.getCenterOf(worldPosition);
		RandomSource r = level.random;
		for (int i = 0; i < 20; i++) {
			Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
				.multiply(1, .25f, 1)
				.normalize();
			Vec3 v = c.add(offset.scale(.5 + r.nextDouble() * .125f))
				.add(0, .125, 0);
			Vec3 m = offset.scale(1 / 32f);

			level.addParticle(soulFlame ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, v.x, v.y, v.z, m.x, m.y, m.z);
		}
	}

	public enum FuelType {
		NONE, NORMAL, SPECIAL
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (level == null) return false;
		ObservePacketPayload.send(worldPosition, 0);
		containedFluidTooltip(tooltip, isPlayerSneaking, level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null));
		if (TimeRemainingPacketPayload.clientTimeRemaining > 20) CALang.builder().add(Component.literal(" ").append(Component.translatable(CreateAddition.MODID + ".tooltip.liquid_burning.time_remaining").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(" " + Util.formatTime(TimeRemainingPacketPayload.clientTimeRemaining)).withStyle(ChatFormatting.AQUA))).forGoggles(tooltip);
		return true;
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
		TimeRemainingPacketPayload.send(remainingBurnTime, player);
	}
}
