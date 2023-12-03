package com.mrh0.createaddition.compat.emi;

import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class ChargingCategory extends CreateEmiRecipe<ChargingRecipe> {

    public ChargingCategory(ChargingRecipe recipe) {
        super(CreateAdditionEMI.CHARGING, recipe, 177, 61);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        //input
        addSlot(widgets, EmiStack.of(recipe.ingredient.getItems()[0]), 14, 8);
        addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 43, 2);

        addTexture(widgets, AllGuiTextures.JEI_LIGHT, 32, 50);
        CAEmiAnimations.addTeslaCoil(widgets, 41, 50, Direction.DOWN, false, -2);
        widgets.addText(Component.literal((Util.format(recipe.energy) + "fe")), 86, 10, 5636095, true);
        addTexture(widgets, AllGuiTextures.JEI_ARROW, 85, 32);

        //output
        for (int i = 0; i < output.size(); i++) {
            int xOff = (i % 2) * 19;
            int yOff = (i / 2) * -19;
            addSlot(widgets, output.get(i), 133 + xOff, 27 + yOff).recipeContext(this);
        }

    }
}