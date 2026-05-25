package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import com.mrh0.createaddition.config.CommonConfig;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LiquidBlazeScrollValueBehaviourLiquid extends ScrollValueBehaviour {

	public static final BehaviourType<LiquidBlazeScrollValueBehaviourLiquid> TYPE = new BehaviourType<>();
		
	@Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

	public LiquidBlazeScrollValueBehaviourLiquid(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
		super(label, be, slot);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		int maxMilliBuckets = CommonConfig.LIQUID_BLAZE_BURNER_MAX_LIQUID_CAPACITY.get();
		int maxBuckets = maxMilliBuckets / 1000;
		return new ValueSettingsBoard(label,Math.max(maxBuckets, 10),1,
			CreateLang.translatedOptions("generic.unit", "millibuckets","buckets"),
			new ValueSettingsFormatter(this::formatSettings));
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    	nbt.putInt("LiquidCapacityValue", value);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    	if (nbt.contains("LiquidCapacityValue"))
        	value = nbt.getInt("LiquidCapacityValue");
    	else
        	value = CommonConfig.LIQUID_BLAZE_BURNER_MAX_LIQUID_CAPACITY.get();
	}

	@Override
	public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
		int value = valueSetting.value();
		int multiplier = switch (valueSetting.row()) {
		case 0 -> 100;
		default -> 1000;
		};
		if (!valueSetting.equals(getValueSettings()))
			playFeedbackSound(this);
		setValue(Math.max(100, value*multiplier));
	}

	@Override
	public ValueSettings getValueSettings() {
		int row = 0;
		int value = this.value;

		if (value >= 1000) {
			return new ValueSettings(1,value / 1000);
		}

		return new ValueSettings(1,value / 100);
	}

	public MutableComponent formatSettings(ValueSettings settings) {
		int value = Math.max(1, settings.value());
		return Component.literal(switch (settings.row()) {
		case 0 -> value*100 + "mB";
		default -> value + "B";
		});
	}

	public int netId() {
    return 2;
	}
}
