package com.mrh0.createaddition.compat.emi;

import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Lang;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class LiquidBurningCategory extends CreateEmiRecipe<LiquidBurningRecipe> {

    public LiquidBurningCategory(LiquidBurningRecipe recipe) {
        super(CreateAdditionEMI.LIQUID_BURNING, recipe, 177, 61);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        //input
        List<ItemStack> buckets = recipe.getFluidIngredient().getMatchingFluidStacks().stream()
                .filter(Objects::nonNull)
                .map((e) -> new ItemStack(e.getFluid().getBucket()))
                .toList();

        addSlot(widgets, EmiStack.of(CAItems.STRAW), width / 2 -36, 3);
        for (ItemStack bucket : buckets) addSlot(widgets, EmiStack.of(bucket), width / 2 -16, 3);
        addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, width / 2 + 3, 8);


        //output
        HeatCondition requiredHeat = recipe.isSuperheated() ? HeatCondition.SUPERHEATED : HeatCondition.HEATED;
        widgets.addText(Lang.translateDirect(requiredHeat.getTranslationKey()).getVisualOrderText(), 9, 86 - 45, requiredHeat.getColor(), true);
        addTexture(widgets, AllGuiTextures.JEI_LIGHT, 81, 58 + 30 - 45);
        addTexture(widgets, AllGuiTextures.JEI_HEAT_BAR, 4, 80 - 45);
        CreateEmiAnimations.addBlazeBurner(widgets, width / 2 + 3, 55 - 45, requiredHeat.visualizeAsBlazeBurner());

        widgets.addText(Component.literal("" + formatTime(recipe.getBurnTime())).getVisualOrderText(), widgets.getWidth() / 2 + 48, 86 - 45, 4210752, false);
    }


    public static String formatTime(int ticks) {
        if (ticks > 20*60) return (ticks/(20*60)) + " min";
        if (ticks > 20) return (ticks/20) + " sec";
        return (ticks) + " ticks";
    }
}