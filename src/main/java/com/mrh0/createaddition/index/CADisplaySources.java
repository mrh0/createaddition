package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterDisplaySource;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorDisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.mrh0.createaddition.CreateAddition.REGISTRATE;

public class CADisplaySources {
    public static final RegistryEntry<ModularAccumulatorDisplaySource> MODULAR_ACCUMULATOR = REGISTRATE.displaySource("modular_accumulator", ModularAccumulatorDisplaySource::new)
            //.associate(CABlocks.MODULAR_ACCUMULATOR.get())
            .register();

    public static final RegistryEntry<DigitalAdapterDisplaySource> DIGITAL_ADAPTER = REGISTRATE.displaySource("digital_adapter", DigitalAdapterDisplaySource::new)
            //.associate(CABlocks.DIGITAL_ADAPTER.get())
            .register();

    public static void register() {}
}
