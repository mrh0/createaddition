package com.mrh0.createaddition.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class CAContinuousSound extends AbstractTickableSoundInstance {
	private final float sharedPitch;
	private final CASoundScape soundScape;
	private final float relativeVolume;

	protected CAContinuousSound(SoundEvent event, CASoundScape soundScape, float sharedPitch, float relativeVolume) {
		super(event, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.soundScape = soundScape;
		this.sharedPitch = sharedPitch;
		this.relativeVolume = relativeVolume;
		this.looping = true;
		this.delay = 0;
		this.relative = false;
	}

	public void remove() {
		stop();
	}

	@Override
	public float getVolume() {
		return soundScape.getVolume() * relativeVolume;
	}

	@Override
	public float getPitch() {
		return sharedPitch;
	}

	@Override
	public double getX() {
		return soundScape.getMeanPos().x;
	}

	@Override
	public double getY() {
		return soundScape.getMeanPos().y;
	}

	@Override
	public double getZ() {
		return soundScape.getMeanPos().z;
	}

	@Override
	public void tick() {}
}