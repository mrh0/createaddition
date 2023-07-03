package com.mrh0.createaddition.compat.rei;

import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.category.WidgetUtil;
import com.simibubi.create.compat.rei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Lang;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LiquidBurningCategory extends CreateRecipeCategory<LiquidBurningRecipe> {

    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public LiquidBurningCategory(Info<LiquidBurningRecipe> info) {
        super(info);
    }

    @Override
    public void addWidgets(CreateDisplay<LiquidBurningRecipe> display, List<Widget> ingredients, Point origin, Rectangle bounds) {
        LiquidBurningRecipe recipe = display.getRecipe();
        List<ItemStack> buckets = recipe.getFluidIngredient().getMatchingFluidStacks().stream()
                .filter(e -> e != null)
                .map((e) -> new ItemStack(e.getFluid().getBucket()))
                .toList();
        ingredients.add(basicSlot(bounds.getWidth() / 2 -56, 3, origin).markInput().entries(display.getInputEntries().get(0)));
        ingredients.add(WidgetUtil.textured(getRenderedSlot(recipe, 0), origin.getX() + bounds.getWidth() / 2 -57, origin.getY() + 2));
        ingredients.add(basicSlot(bounds.getWidth() / 2 -36, 3, origin).markInput().disableBackground().entries(display.getInputEntries().get(1)));
        ingredients.add(WidgetUtil.textured(getRenderedSlot(recipe, 0), origin.getX() + bounds.getWidth() / 2 -37, origin.getY() + 2));
        Slot fluidSlot = basicSlot(bounds.getWidth() / 2 -16, 3, origin).markInput().disableBackground().entries(display.getInputEntries().get(2));
        setFluidTooltip(fluidSlot);
        ingredients.add(WidgetUtil.textured(getRenderedSlot(recipe, 0), origin.getX() + bounds.getWidth() / 2 -17, origin.getY() + 2));
        ingredients.add(fluidSlot);
    }

    @Override
    public List<Widget> setupDisplay(CreateDisplay<LiquidBurningRecipe> display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createDrawableWidget((helper, stack, mouseX, mouseY, partialTick) -> {
            stack.pushPose();
            stack.translate(bounds.getX(), bounds.getY() + 4, 0);
            LiquidBurningRecipe recipe = display.getRecipe();
            ClientMinecraftWrapper.getFont().draw(stack, formatTime(recipe.getBurnTime()), bounds.getWidth() / 2 + 48, 86 - 50, 4210752);


            HeatCondition requiredHeat = recipe.isSuperheated() ? HeatCondition.SUPERHEATED : HeatCondition.HEATED;

            AllGuiTextures.JEI_LIGHT.render(stack, 81, 58 + 30 - 50);

            AllGuiTextures.JEI_HEAT_BAR.render(stack, 4, 80 - 50);
            ClientMinecraftWrapper.getFont().draw(stack, Lang.translateDirect(requiredHeat.getTranslationKey()), 9,
                    86 - 50, requiredHeat.getColor());

            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(stack, bounds.getWidth() / 2 + 3, 55 - 50);

            AllGuiTextures.JEI_DOWN_ARROW.render(stack, bounds.getWidth() / 2 + 3, 8);
            stack.popPose();
        }));
        addWidgets(display, widgets, new Point(bounds.getX(), bounds.getY() + 4));
        addWidgets(display, widgets, new Point(bounds.getX(), bounds.getY() + 4), bounds);
        return widgets;
    }

    public static String formatTime(int ticks) {
        if (ticks > 20*60) return (ticks/(20*60)) + " min";
        if (ticks > 20) return (ticks/20) + " sec";
        return (ticks) + " ticks";
    }
}
