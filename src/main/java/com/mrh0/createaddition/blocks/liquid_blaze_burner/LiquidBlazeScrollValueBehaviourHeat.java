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

public class LiquidBlazeScrollValueBehaviourHeat extends ScrollValueBehaviour {

	public static final BehaviourType<LiquidBlazeScrollValueBehaviourHeat> TYPE = new BehaviourType<>();

	@Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

	public LiquidBlazeScrollValueBehaviourHeat(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
		super(label, be, slot);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		int maxTicks = CommonConfig.LIQUID_BLAZE_BURNER_MAX_HEAT_CAPACITY.get();
    	int maxMinutes = maxTicks / (60 * 20);
		return new ValueSettingsBoard(label,Math.max(maxMinutes, 60), 10,
			CreateLang.translatedOptions("generic.unit", "ticks", "seconds", "minutes"),
			new ValueSettingsFormatter(this::formatSettings));
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    	nbt.putInt("HeatCapacityValue", value);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    	value = nbt.getInt("HeatCapacityValue");
	}

	@Override
	public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
		int value = valueSetting.value();
		int multiplier = switch (valueSetting.row()) {
		case 0 -> 1;
		case 1 -> 20;
		default -> 60 * 20;
		};
		if (!valueSetting.equals(getValueSettings()))
			playFeedbackSound(this);
		setValue(Math.max(2, Math.max(1, value) * multiplier));
	}

	@Override
	public ValueSettings getValueSettings() {
		int row = 0;
		int value = this.value;

		if (value > 60 * 20) {
			value = value / (60 * 20);
			row = 2;
		} else if (value > 60) {
			value = value / 20;
			row = 1;
		}

		return new ValueSettings(row, value);
	}

	public MutableComponent formatSettings(ValueSettings settings) {
		int value = Math.max(1, settings.value());
		return Component.literal(switch (settings.row()) {
		case 0 -> Math.max(2, value) + "t";
		case 1 -> "0:" + (value < 10 ? "0" : "") + value;
		default -> value + ":00";
		});
	}

	public int netId() {
    return 1;
	}
}
