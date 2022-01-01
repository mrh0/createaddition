/*
package com.mrh0.createaddition.entities.overcharged_hammer;

import javax.annotation.Nullable;

import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.index.CAEntities;
import com.mrh0.createaddition.index.CAItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class OverchargedHammerEntity extends AbstractArrow {
	private static final EntityDataAccessor<Byte> LOYALTY_LEVEL = SynchedEntityData
			.defineId(OverchargedHammerEntity.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData
			.defineId(OverchargedHammerEntity.class, EntityDataSerializers.BOOLEAN);
	private ItemStack thrownStack = new ItemStack(CAItems.OVERCHARGED_HAMMER.get());
	private boolean dealtDamage;
	public int returningTicks;

	@SuppressWarnings("unchecked")
	public OverchargedHammerEntity(EntityType<?> type, Level world) {
		super((EntityType<? extends AbstractArrow>) type, world);
	}

	public OverchargedHammerEntity(Level world, LivingEntity living, ItemStack stack) {
		super(CAEntities.OVERCHARGED_HAMMER_ENTITY.get(), living, world);
		this.thrownStack = stack.copy();
		this.entityData.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyalty(stack));
		this.entityData.set(ENCHANTED, stack.hasFoil());
	}

	public OverchargedHammerEntity(Level world, double x, double y, double z) {
		super(CAEntities.OVERCHARGED_HAMMER_ENTITY.get(), x, y, z, world);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(LOYALTY_LEVEL, (byte) 0);
		this.entityData.define(ENCHANTED, false);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
			int i = 3;
			this.setNoPhysics(true);
			Vec3 vector3d = new Vec3(entity.getX() - this.getX(), entity.getEyeY() - this.getY(),
					entity.getZ() - this.getZ());
			this.setPosRaw(this.getX(), this.getY() + vector3d.y * 0.015D * (double) i, this.getZ());
			if (this.level.isClientSide) {
				this.yOld = this.getY();
			}

			double d0 = 0.05D * (double) i;
			this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vector3d.normalize().scale(d0)));
			if (this.returningTicks == 0) {
				// this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
			}

			++this.returningTicks;
		}

		super.tick();
	}

	@Override
	protected ItemStack getPickupItem() {
		return this.thrownStack.copy();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isEnchanted() {
		return this.entityData.get(ENCHANTED);
	}

	@Nullable
	@Override
	protected EntityHitResult findHitEntity(Vec3 v1, Vec3 v2) {
		return this.dealtDamage ? null : super.findHitEntity(v1, v2);
	}


	@Override
	protected void onHitEntity(EntityHitResult entityRay) {
		Entity hitEntity = entityRay.getEntity();
		float f = 8.0F;
		if (hitEntity instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) hitEntity;
			f += EnchantmentHelper.getDamageBonus(this.thrownStack, livingentity.getMobType());
		}

		Entity ownerEntity = this.getOwner();
		DamageSource damagesource = DamageSource.trident(this, (Entity) (ownerEntity == null ? this : ownerEntity));
		this.dealtDamage = true;
		SoundEvent soundevent = SoundEvents.ANVIL_HIT;
		if (hitEntity.hurt(damagesource, f)) {
			if (hitEntity instanceof LivingEntity) {
				LivingEntity hitLivingEntity = (LivingEntity) hitEntity;
				if (ownerEntity instanceof LivingEntity) {
					if (hitLivingEntity instanceof Player)
						hitLivingEntity.addEffect(new MobEffectInstance(CAEffects.SHOCKING, 20));
					else
						hitLivingEntity.addEffect(new MobEffectInstance(CAEffects.SHOCKING, 40));
					EnchantmentHelper.doPostHurtEffects(hitLivingEntity, ownerEntity);
					EnchantmentHelper.doPostDamageEffects((LivingEntity) ownerEntity, hitLivingEntity);
				}

				this.doPostHurtEffects(hitLivingEntity);
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
		float f1 = 1.0F;
		if (this.level instanceof ServerLevel && this.level.isThundering()
				&& EnchantmentHelper.hasChanneling(this.thrownStack)) {
			BlockPos blockpos = hitEntity.blockPosition();
			if (this.level.canSeeSky(blockpos)) {
				LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
				lightningboltentity.moveTo(Vec3.atBottomCenterOf(blockpos));
				lightningboltentity.setCause(ownerEntity instanceof ServerPlayer ? (ServerPlayer) ownerEntity : null);
				this.level.addFreshEntity(lightningboltentity);
				soundevent = SoundEvents.TRIDENT_THUNDER;
				f1 = 5.0F;
			}
		}

		this.playSound(soundevent, f1, 1.0F);
	}


	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.ANVIL_LAND;
	}

	@Override
	public void playerTouch(Player player) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUUID() == player.getUUID()) {
			super.playerTouch(player);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("Hammer", 10)) {
			this.thrownStack = ItemStack.of(nbt.getCompound("Hammer"));
		}

		this.dealtDamage = nbt.getBoolean("DealtDamage");
		this.entityData.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyalty(this.thrownStack));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.put("Hammer", this.thrownStack.save(new CompoundTag()));
		nbt.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void tickDespawn() {
		int i = this.entityData.get(LOYALTY_LEVEL);
		if (this.pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
			super.tickDespawn();
		}
	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}

	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		@SuppressWarnings("unchecked")
		EntityType.Builder<OverchargedHammerEntity> entityBuilder = (EntityType.Builder<OverchargedHammerEntity>) builder;
		return entityBuilder.sized(0.25f, 0.25f);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean isOnFire() {
		return false;
	}
}*/
