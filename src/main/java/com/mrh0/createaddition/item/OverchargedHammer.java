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
		builder.put(Attributes.GENERIC_ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 9.0D, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.GENERIC_ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)-3.1F, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity living, int a) {
		if (living instanceof PlayerEntity) {
			PlayerEntity playerentity = (PlayerEntity) living;
			int i = this.getUseDuration(stack) - a;
			if (i >= 10) {
				if (!world.isRemote) {
					stack.damageItem(1, playerentity, (ent) -> {
						ent.sendBreakAnimation(living.getActiveHand());
					});
					OverchargedHammerEntity hammerentity = new OverchargedHammerEntity(world, playerentity, stack);
					hammerentity.setProperties(playerentity, playerentity.rotationPitch,
							playerentity.rotationYaw, 0.0F, 2.5F + (float) 1f * 0.5F, 1.0F);
					if (playerentity.abilities.isCreativeMode) {
						hammerentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
					}

					world.addEntity(hammerentity);
					world.playMovingSound((PlayerEntity) null, hammerentity,
							SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
					if (!playerentity.abilities.isCreativeMode) {
						playerentity.inventory.deleteStack(stack);
					}
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1) {
			return ActionResult.fail(itemstack);
		} else {
			player.setActiveHand(hand);
			return ActionResult.consume(itemstack);
		}
	}

	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity livingA, LivingEntity livingB) {
		stack.damageItem(1, livingB, (ent) -> {
			ent.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity living) {
		if ((double) state.getBlockHardness(world, pos) != 0.0D) {
			stack.damageItem(2, living, (ent) -> {
				ent.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
		return slot == EquipmentSlotType.MAINHAND ? this.attributeModifiers
				: super.getAttributeModifiers(slot);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		return 1024;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == CAItems.OVERCHARGED_ALLOY.get();
	}
	
	
}