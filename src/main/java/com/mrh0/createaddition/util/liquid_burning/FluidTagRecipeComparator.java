package com.mrh0.createaddition.util.liquid_burning;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;

/**
 * This class allows a tag name to be its own recipe builder, in this instance,
 * specifically regarding fluid fuels for the Liquid Blaze Burner. In order to
 * add your own recipe using this, just name your tag to this:
 * ["burnable_fuel_{YOUR_BURNING_TIME}"].
 * The semantics are open to change as support for "heated" and "superheated"
 * needs to added as well. So in the future, your tag names would look like this:
 * ["burnable_fuel_{HEAT_TYPE}_{YOUR_BURNING_TIME}"]. But the old semantic
 * will still be supported, and simply just default to "heated". So you
 * don't need to worry about breaking changes.
 */
public final class FluidTagRecipeComparator {

   public static boolean argsToTag(Fluid fluid, Args args) {
       for (TagKey<Fluid> tagKey : fluid.defaultFluidState().getTags().toList()) {
           int i;
           boolean crashOnLow = true;
           try {
               i = Integer.parseInt(tagKey
                       .toString()
                       .replaceAll("[^0-9]+", " ")
                       .trim()
               );
           } catch (NumberFormatException e) {
               i = 0;
               crashOnLow = false;
           }
           BurnableTagProperties tagProperties = new BurnableTagProperties(
                   crashOnLow,
                   i
           );
           Boolean bl = args.args(tagProperties, tagKey);
           if (bl != null && bl && tagProperties.getTime() != 0) {
               return true;
           }
       }
       return false;
   }

    public interface Args {
        @Nullable Boolean args(BurnableTagProperties tagProperties, TagKey<Fluid> tagKey);
    }
}
