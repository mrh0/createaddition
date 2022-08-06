package com.mrh0.createaddition.compat.rei.category;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

public class RollingMillCategory extends CARecipeCategory<RollingRecipe> {

    private final AnimatedRollingMill rolling_mill = new AnimatedRollingMill();

    public RollingMillCategory(Info<?> info) {
        super((Info<RollingRecipe>) info, "charging");
    }


    @Override
    public void addWidgets(CreateDisplay<RollingRecipe> display, List<Widget> ingredients, Point origin) {
        ingredients.add(basicSlot(origin.x + 15, origin.y + 9)
                .markInput()
                .entries(EntryIngredients.ofIngredient(display.getRecipe().getIngredient())));

        ItemStack result = display.getRecipe().getResultItem();
        int yOffset = 0;

        ingredients.add(basicSlot(origin.x + 140, origin.y + 28 + yOffset)
                .markOutput()
                .entries(EntryIngredients.of(result)));
    }

    @Override
    public void draw(RollingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SLOT.render(matrixStack, 14, 8);
        AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
        rolling_mill.draw(matrixStack, 48, 27);

        getRenderedSlot(recipe, 0).render(matrixStack, 139, 27);
    }
}
