package com.mrh0.createaddition.compat.emi;

import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.emi.emi.api.widget.WidgetHolder;

public class RollingMillCategory extends CreateEmiRecipe<RollingRecipe> {

    public RollingMillCategory(RollingRecipe recipe) {
        super(CreateAdditionEMI.ROLLING, recipe, 177, 61);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        //input
        addSlot(widgets, input.get(0), 14, 8);
        addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 43, 4);
        addTexture(widgets, AllGuiTextures.JEI_SHADOW, 30, 43);
        CAEmiAnimations.addRoller(widgets, 46, 45, -15);
        addTexture(widgets, AllGuiTextures.JEI_ARROW, 85, 32);

        //output
        for (int i = 0; i < output.size(); i++) {
            int xOff = (i % 2) * 19;
            int yOff = (i / 2) * -19;
            addSlot(widgets, output.get(i), 133 + xOff, 27 + yOff).recipeContext(this);
        }
    }
}