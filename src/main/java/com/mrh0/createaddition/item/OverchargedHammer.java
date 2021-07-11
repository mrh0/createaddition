package com.mrh0.createaddition.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mrh0.createaddition.entities.overcharged_hammer.OverchargedHammerEntity;
import com.mrh0.createaddition.index.CAItems;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OverchargedHammer extends Item implements IVanishable {
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public OverchargedHammer(Item.Properties props) {
		super(props);
	 	Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 9.0D, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)-3.1F, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean canAttackBlock(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void releaseUsing(ItemStack stack, World world, LivingEntity living, int a) {
		if (living instanceof PlayerEntity) {
			PlayerEntity playerentity = (PlayerEntity) living;
			int i = this.getUseDuration(stack) - a;
			if (i >= 10) {
				if (!world.isClientSide) {
					stack.hurtAndBreak(1, playerentity, (ent) -> {
						ent.broadcastBreakEvent(living.getUsedItemHand());
					});
					OverchargedHammerEntity hammerentity = new OverchargedHammerEntity(world, playerentity, stack);
					hammerentity.shootFromRotation(playerentity, playerentity.xRot,
							playerentity.yRot, 0.0F, 2.5F + (float) 1f * 0.5F, 1.0F);
					if (playerentity.abilities.instabuild) {
						hammerentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
					}

					world.addFreshEntity(hammerentity);
					world.playSound((PlayerEntity) null, hammerentity,
							SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
					if (!playerentity.abilities.instabuild) {
						playerentity.inventory.removeItem(stack);
					}
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
			return ActionResult.fail(itemstack);
		} else {
			player.startUsingItem(hand);
			return ActionResult.consume(itemstack);
		}
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity livingA, LivingEntity livingB) {
		stack.hurtAndBreak(1, livingB, (ent) -> {
			ent.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
		});
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity living) {
		if ((double) state.getDestroySpeed(world, pos) != 0.0D) {
			stack.hurtAndBreak(2, living, (ent) -> {
				ent.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot) {
		return slot == EquipmentSlotType.MAINHAND ? this.attributeModifiers
				: super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		return 1024;
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == CAItems.OVERCHARGED_ALLOY.get();
	}
	
	
}