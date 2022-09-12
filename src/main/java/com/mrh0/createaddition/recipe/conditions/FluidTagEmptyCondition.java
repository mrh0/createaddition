package com.mrh0.createaddition.recipe.conditions;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public class FluidTagEmptyCondition implements ICondition {
	private static final ResourceLocation NAME = new ResourceLocation("createaddition", "fluidtag_empty");
	private final ResourceLocation tag_name;

	public FluidTagEmptyCondition(String location) {
		this(new ResourceLocation(location));
	}

	public FluidTagEmptyCondition(String namespace, String path) {
		this(new ResourceLocation(namespace, path));
	}

	public FluidTagEmptyCondition(ResourceLocation tag) {
		this.tag_name = tag;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		//TagKey<Fluid> tag = TagKey.//	//TagCollectionManager.getInstance().getFluids().getTag(tag_name);
		
		@NotNull ITag<Fluid> tag = ForgeRegistries.FLUIDS.tags().getTag(FluidTags.create(tag_name));
		//System.out.println("fluidTag:" + tag_name + ":" + (tag == null || tag.getValues().isEmpty()));
		return tag == null || tag.isEmpty();
	}

	@Override
	public String toString() {
		return "fluidtag_empty(\"" + tag_name + "\")";
	}

	public static class Serializer implements IConditionSerializer<FluidTagEmptyCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, FluidTagEmptyCondition value) {
			json.addProperty("fluidTag", value.tag_name.toString());
		}

		@Override
		public FluidTagEmptyCondition read(JsonObject json) {
			return new FluidTagEmptyCondition(new ResourceLocation(GsonHelper.getAsString(json, "fluidTag")));
		}

		@Override
		public ResourceLocation getID() {
			return FluidTagEmptyCondition.NAME;
		}
	}
}
