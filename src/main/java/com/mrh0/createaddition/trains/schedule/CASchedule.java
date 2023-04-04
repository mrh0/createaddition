package com.mrh0.createaddition.trains.schedule;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.trains.schedule.condition.EnergyThresholdCondition;
import com.simibubi.create.content.logistics.trains.management.schedule.Schedule;
import com.simibubi.create.content.logistics.trains.management.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CASchedule {
    static {
        registerCondition("energy_threshold", EnergyThresholdCondition::new);
    }

    private static void registerCondition(String name, Supplier<? extends ScheduleWaitCondition> factory) {
        Schedule.CONDITION_TYPES.add(Pair.of(CreateAddition.asResource(name), factory));
    }

    public static void register() {}
}
