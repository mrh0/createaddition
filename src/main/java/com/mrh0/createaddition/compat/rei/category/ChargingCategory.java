package com.mrh0.createaddition.compat.rei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ChargingCategory extends CARecipeCategory<ChargingRecipe> {

    private final AnimatedTeslaCoil tesla_coil = new AnimatedTeslaCoil();

    public ChargingCategory(Info<ChargingRecipe> info, String name) {
        super(info, name);
    }

    @Override
    public void addWidgets(CreateDisplay<ChargingRecipe> display, List<Widget> ingredients, Point origin) {
        ingredients.add(basicSlot(origin.x + 15, origin.y + 9)
                .markInput()
                .entries(EntryIngredients.of(display.getRecipe().ingredient.getItems()[0])));

        ItemStack result = display.getRecipe().getResultItem();
        int yOffset = 0;

        ingredients.add(basicSlot(origin.x + 140, origin.y + 28 + yOffset)
                .markOutput()
                .entries(EntryIngredients.of(result)));
    }

    @Override
    public void draw(ChargingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SLOT.render(matrixStack, 14, 8);
        AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
        tesla_coil.draw(matrixStack, 48, 27);

        Minecraft.getInstance().font.draw(matrixStack, Util.format(recipe.energy) + "fe", 86, 9, 4210752);

        getRenderedSlot(recipe, 0).render(matrixStack, 139, 27);
    }
}