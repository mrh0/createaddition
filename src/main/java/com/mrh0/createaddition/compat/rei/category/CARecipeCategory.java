package com.mrh0.createaddition.compat.rei.category;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CARecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {
    private final String name;
    public CARecipeCategory(Info<T> info, String name) {
        super(info);
        this.name = name;

    }
    @Override
    public CategoryIdentifier<CreateDisplay<T>> getCategoryIdentifier() {
        return CategoryIdentifier.of(new ResourceLocation(CreateAddition.MODID, name));
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent( CreateAddition.MODID + ".recipe." + name);
    }

}