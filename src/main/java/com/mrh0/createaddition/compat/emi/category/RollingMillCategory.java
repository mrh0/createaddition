package com.mrh0.createaddition.compat.emi.category;

import com.mrh0.createaddition.compat.emi.CreateAdditionEMI;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

public class RollingMillCategory extends CreateEmiRecipe<RollingRecipe> {
    public RollingMillCategory(RollingRecipe recipe) {
        super(CreateAdditionEMI.RollingMill, recipe, 177, 53,c->{});
        this.input = List.of(EmiIngredient.of(recipe.getIngredient()));
        this.output = List.of(EmiStack.of(recipe.getResultItem()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 43, 4);
        addTexture(widgets, AllGuiTextures.JEI_ARROW, 85, 32);
        addTexture(widgets, AllGuiTextures.JEI_SHADOW, 32, 40);

        addSlot(widgets, input.get(0), 15, 9);


        addSlot(widgets, output.get(0), 140, 28).recipeContext(this);


        AnimatedRollingMill.addRollingMill(widgets, 48, 44);
    }
}
