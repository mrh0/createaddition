package com.mrh0.createaddition.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ElectrumAmulet extends Item {
    public ElectrumAmulet(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int pSlotId, boolean pIsSelected) {
        if(!(entity instanceof Player player)) return;
        if(player.getHealth() <= 8) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 3, 0, true, true, true));
        }
    }
}
