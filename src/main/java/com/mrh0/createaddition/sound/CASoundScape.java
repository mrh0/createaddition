package com.mrh0.createaddition.sound;


import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import com.mrh0.createaddition.sound.CASoundScapes.AmbienceGroup;
import com.mrh0.createaddition.sound.CASoundScapes.PitchGroup;

import java.util.ArrayList;
import java.util.List;

class CASoundScape {
	List<CAContinuousSound> continuous;
//	List<RepeatingSound> repeating;
	private float pitch;
	private AmbienceGroup group;
	private Vec3 meanPos;
	private PitchGroup pitchGroup;

	public CASoundScape(float pitch, AmbienceGroup group) {
		this.pitchGroup = CASoundScapes.getGroupFromPitch(pitch);
		this.pitch = pitch;
		this.group = group;
		continuous = new ArrayList<>();
//		repeating = new ArrayList<>();
	}

	public CASoundScape continuous(SoundEvent sound, float relativeVolume, float relativePitch) {
		return add(new CAContinuousSound(sound, this, pitch * relativePitch, relativeVolume));
	}

//	public CASoundScape repeating(SoundEvent sound, float relativeVolume, float relativePitch, int delay) {
//		return add(new RepeatingSound(sound, this, pitch * relativePitch, relativeVolume, delay));
//	}

	public CASoundScape add(CAContinuousSound continuousSound) {
		continuous.add(continuousSound);
		return this;
	}

//	public CASoundScape add(RepeatingSound repeatingSound) {
//		repeating.add(repeatingSound);
//		return this;
//	}

	public void play() {
		for (CAContinuousSound ting : continuous) {
			Sound sound = ting.getSound();
			if (sound == null) continue;
			CreateAddition.LOGGER.info(sound.getPath().toString());
		}
		continuous.forEach(Minecraft.getInstance()
			.getSoundManager()::play);
	}

	public void tick() {
		if (AnimationTickHolder.getTicks() % CASoundScapes.UPDATE_INTERVAL == 0)
			meanPos = null;
//		repeating.forEach(RepeatingSound::tick);
	}

	public void remove() {
		continuous.forEach(CAContinuousSound::remove);
	}

	public Vec3 getMeanPos() {
		return meanPos == null ? meanPos = determineMeanPos() : meanPos;
	}

	private Vec3 determineMeanPos() {
		meanPos = Vec3.ZERO;
		int amount = 0;
		for (BlockPos blockPos : CASoundScapes.getAllLocations(group, pitchGroup)) {
			meanPos = meanPos.add(VecHelper.getCenterOf(blockPos));
			amount++;
		}
		if (amount == 0)
			return meanPos;
		return meanPos.scale(1f / amount);
	}

	public float getVolume() {
		Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
		float distanceMultiplier = 0;
		if (renderViewEntity != null) {
			double distanceTo = renderViewEntity.position()
				.distanceTo(getMeanPos());
			distanceMultiplier = (float) Mth.lerp(distanceTo / CASoundScapes.MAX_AMBIENT_SOURCE_DISTANCE, 2, 0);
		}
		int soundCount = CASoundScapes.getSoundCount(group, pitchGroup);
		float max = AllConfigs.client().ambientVolumeCap.getF();
		float argMax = (float) CASoundScapes.SOUND_VOLUME_ARG_MAX;
		return Mth.clamp(soundCount / (argMax * 10f), 0.025f, max) * distanceMultiplier;
	}
}