package com.mrh0.createaddition.recipe.conditions;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class HasFluidTagCondition implements ICondition {
	private static final ResourceLocation NAME = new ResourceLocation("createaddition", "hasFluidTag");
	private final ResourceLocation tagName;

	public HasFluidTagCondition(String location) {
		this(new ResourceLocation(location));
	}

	public HasFluidTagCondition(String namespace, String path) {
		this(new ResourceLocation(namespace, path));
	}

	public HasFluidTagCondition(ResourceLocation tag) {
		this.tagName = tag;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}
	
	@Override
	public String toString() {
		return "hasFluidTag(\"" + tagName + "\")";
	}

	public static class Serializer implements IConditionSerializer<HasFluidTagCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, HasFluidTagCondition value) {
			json.addProperty("fluidTag", value.tagName.toString());
		}

		@Override
		public HasFluidTagCondition read(JsonObject json) {
			return new HasFluidTagCondition(new ResourceLocation(GsonHelper.getAsString(json, "fluidTag")));
		}

		@Override
		public ResourceLocation getID() {
			return HasFluidTagCondition.NAME;
		}
	}

	@Override
	public boolean test(IContext context) {
		return ForgeRegistries.FLUIDS.containsKey(tagName);
	}
}
