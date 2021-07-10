package com.mrh0.createaddition.entities.overcharged_hammer;

import javax.annotation.Nullable;

import com.mrh0.createaddition.index.CAEntities;
import com.mrh0.createaddition.index.CAItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class OverchargedHammerEntity extends AbstractArrowEntity {
	private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(OverchargedHammerEntity.class,
			DataSerializers.BYTE);
	private static final DataParameter<Boolean> ENCHANTED = EntityDataManager.createKey(OverchargedHammerEntity.class,
			DataSerializers.BOOLEAN);
	private ItemStack thrownStack = new ItemStack(CAItems.OVERCHARGED_HAMMER.get());
	private boolean dealtDamage;
	public int returningTicks;
	
	public OverchargedHammerEntity(EntityType<OverchargedHammerEntity> type, World world) {
		super(type, world);
	}

	public OverchargedHammerEntity(World world, LivingEntity living, ItemStack stack) {
		super(CAEntities.OVERCHARGED_HAMMER_ENTITY.get(), living, world);
		this.thrownStack = stack.copy();
		this.dataManager.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyaltyModifier(stack));
		this.dataManager.set(ENCHANTED, stack.hasEffect());
	}

	public OverchargedHammerEntity(World world, double x, double y, double z) {
		super(CAEntities.OVERCHARGED_HAMMER_ENTITY.get(), x, y, z, world);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(LOYALTY_LEVEL, (byte) 0);
		this.dataManager.register(ENCHANTED, false);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		if (this.timeInGround > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.getNoClip()) && entity != null) {
			int i = 3;
			this.setNoClip(true);
			Vector3d vector3d = new Vector3d(entity.getX() - this.getX(), entity.getEyeY() - this.getY(),
					entity.getZ() - this.getZ());
			this.setPos(this.getX(), this.getY() + vector3d.y * 0.015D * (double) i, this.getZ());
			if (this.world.isRemote) {
				this.lastTickPosY = this.getY();
			}

			double d0 = 0.05D * (double) i;
			this.setMotion(this.getMotion().scale(0.95D).add(vector3d.normalize().scale(d0)));
			if (this.returningTicks == 0) {
				this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
			}

			++this.returningTicks;
		}

		super.tick();
	}

	@Override
	protected ItemStack getArrowStack() {
		return this.thrownStack.copy();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isEnchanted() {
		return this.dataManager.get(ENCHANTED);
	}

	/**
	 * Gets the EntityRayTraceResult representing the entity hit
	 */
	@Nullable
	@Override
	protected EntityRayTraceResult rayTraceEntities(Vector3d v1, Vector3d v2) {
		return this.dealtDamage ? null : super.rayTraceEntities(v1, v2);
	}

	/**
	 * Called when the arrow hits an entity
	 */
	@Override
	protected void onEntityHit(EntityRayTraceResult entityRay) {
		Entity entity = entityRay.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entity;
			f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
		}

		Entity entity1 = this.getOwner();
		DamageSource damagesource = DamageSource.causeTridentDamage(this, (Entity) (entity1 == null ? this : entity1));
		this.dealtDamage = true;
		SoundEvent soundevent = SoundEvents.BLOCK_ANVIL_HIT;
		if (entity.attackEntityFrom(damagesource, f)) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingentity1 = (LivingEntity) entity;
				if (entity1 instanceof LivingEntity) {
					EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
					EnchantmentHelper.applyArthropodEnchantments((LivingEntity) entity1, livingentity1);
				}

				this.arrowHit(livingentity1);
			}
		}

		this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
		float f1 = 1.0F;
		if (this.world instanceof ServerWorld && this.world.isThundering()
				&& EnchantmentHelper.hasChanneling(this.thrownStack)) {
			BlockPos blockpos = entity.getBlockPos();
			if (this.world.isSkyVisible(blockpos)) {
				LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
				lightningboltentity.refreshPositionAfterTeleport(Vector3d.ofBottomCenter(blockpos));
				lightningboltentity
						.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity1 : null);
				this.world.addEntity(lightningboltentity);
				soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
				f1 = 5.0F;
			}
		}

		this.playSound(soundevent, f1, 1.0F);
	}

	/**
	 * The sound made when an entity is hit by this projectile
	 */
	@Override
	protected SoundEvent getHitEntitySound() {
		return SoundEvents.BLOCK_ANVIL_LAND;
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	@Override
	public void onCollideWithPlayer(PlayerEntity player) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUniqueID() == player.getUniqueID()) {
			super.onCollideWithPlayer(player);
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		if (nbt.contains("Hammer", 10)) {
			this.thrownStack = ItemStack.read(nbt.getCompound("Hammer"));
		}

		this.dealtDamage = nbt.getBoolean("DealtDamage");
		this.dataManager.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyaltyModifier(this.thrownStack));
	}

	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.put("Hammer", this.thrownStack.write(new CompoundNBT()));
		nbt.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void age() {
		int i = this.dataManager.get(LOYALTY_LEVEL);
		if (this.pickupStatus != AbstractArrowEntity.PickupStatus.ALLOWED || i <= 0) {
			super.age();
		}
	}

	@Override
	protected float getWaterDrag() {
		return 0.99F;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}
	
	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		@SuppressWarnings("unchecked")
		EntityType.Builder<OverchargedHammerEntity> entityBuilder = (EntityType.Builder<OverchargedHammerEntity>) builder;
		return entityBuilder.size(0.25f, 0.25f);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
