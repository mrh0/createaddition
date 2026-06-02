package com.mrh0.createaddition.item;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.index.CASounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ElectrumAmuletItem extends Item {
    private static int CHARGE_CHANCE = 300;
    
    public ElectrumAmuletItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int pSlotId, boolean pIsSelected) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;
        if (!CommonConfig.AMULET_EFFECT_ENABLED.get()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack toCharge;
        if (mainHand == stack) toCharge = offHand;
        else if (offHand == stack) toCharge = mainHand;
        else return;

        IEnergyStorage es = toCharge.getCapability(Capabilities.EnergyStorage.ITEM);
        boolean canCharge = es != null && es.receiveEnergy(1, true) > 0;

        if (canCharge && level.random.nextInt(CHARGE_CHANCE) == 0) {
            es.receiveEnergy(CHARGE_CHANCE * CommonConfig.ELECTRUM_AMULET_CHARGE_RATE.get(), false);
            player.addEffect(new MobEffectInstance(CAEffects.SHOCKING, 40, 0, false, true, true));
            if (CommonConfig.AUDIO_ENABLED.get())
                level.playSound(null, player.blockPosition(), CASounds.LITTLE_ZAP.get(), SoundSource.PLAYERS, 0.4f, 1f);
        }
    }
}
