package com.mrh0.createaddition.sound;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CASounds;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.function.BiFunction;

public class CASoundScapes {

	static final int MAX_AMBIENT_SOURCE_DISTANCE = 16;
	static final int UPDATE_INTERVAL = 5;
	static final int SOUND_VOLUME_ARG_MAX = 15;

	public enum AmbienceGroup {
		DYNAMO(CASoundScapes::dynamo),
		TESLA(CASoundScapes::tesla),

		;

		private BiFunction<Float, AmbienceGroup, CASoundScape> factory;

		AmbienceGroup(BiFunction<Float, AmbienceGroup, CASoundScape> factory) {
			this.factory = factory;
		}

		public CASoundScape instantiate(float pitch) {
			return factory.apply(pitch, this);
		}
	}

	private static CASoundScape dynamo(float pitch, AmbienceGroup group) {
		return new CASoundScape(pitch, group).continuous(CASounds.ELECTRIC_MOTOR_BUZZ.get(), 0.75f, 1);
	}

	private static CASoundScape tesla(float pitch, AmbienceGroup group) {
		return new CASoundScape(pitch, group).continuous(CASounds.TESLA_COIL.get(), 1f, 1);
	}


	enum PitchGroup {
		VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH
	}

	private static Map<AmbienceGroup, Map<PitchGroup, Set<BlockPos>>> counter = new IdentityHashMap<>();
	private static Map<Pair<AmbienceGroup, PitchGroup>, CASoundScape> activeSounds = new HashMap<>();

	public static void play(AmbienceGroup group, BlockPos pos, float pitch) {
		if (!AllConfigs.client().enableAmbientSounds.get())
			return;
		if (!outOfRange(pos))
			addSound(group, pos, pitch);
	}

	public static void tick() {
		activeSounds.values()
			.forEach(CASoundScape::tick);

		if (AnimationTickHolder.getTicks() % UPDATE_INTERVAL != 0)
			return;

		boolean disable = !AllConfigs.client().enableAmbientSounds.get();
		for (Iterator<Map.Entry<Pair<AmbienceGroup, PitchGroup>, CASoundScape>> iterator = activeSounds.entrySet()
			.iterator(); iterator.hasNext();) {

			Map.Entry<Pair<AmbienceGroup, PitchGroup>, CASoundScape> entry = iterator.next();
			Pair<AmbienceGroup, PitchGroup> key = entry.getKey();
			CASoundScape value = entry.getValue();

			if (disable || getSoundCount(key.getFirst(), key.getSecond()) == 0) {
				value.remove();
				iterator.remove();
			}
		}

		counter.values()
			.forEach(m -> m.values()
				.forEach(Set::clear));
	}

	private static void addSound(AmbienceGroup group, BlockPos pos, float pitch) {
		PitchGroup groupFromPitch = getGroupFromPitch(pitch);
		Set<BlockPos> set = counter.computeIfAbsent(group, ag -> new IdentityHashMap<>())
			.computeIfAbsent(groupFromPitch, pg -> new HashSet<>());
		set.add(pos);

		Pair<AmbienceGroup, PitchGroup> pair = Pair.of(group, groupFromPitch);
		activeSounds.computeIfAbsent(pair, $ -> {
			CASoundScape soundScape = group.instantiate(pitch);
			soundScape.play();
			return soundScape;
		});
	}

	public static void invalidateAll() {
		counter.clear();
		activeSounds.forEach(($, sound) -> sound.remove());
		activeSounds.clear();
	}

	protected static boolean outOfRange(BlockPos pos) {
		return !getCameraPos().closerThan(pos, MAX_AMBIENT_SOURCE_DISTANCE);
	}

	protected static BlockPos getCameraPos() {
		Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
		if (renderViewEntity == null)
			return BlockPos.ZERO;
		BlockPos playerLocation = renderViewEntity.blockPosition();
		return playerLocation;
	}

	public static int getSoundCount(AmbienceGroup group, PitchGroup pitchGroup) {
		int count = getAllLocations(group, pitchGroup).size();
		CreateAddition.LOGGER.info("count of all sounds in " + group.name() + ": " + count);
		return count;
	}

	public static Set<BlockPos> getAllLocations(AmbienceGroup group, PitchGroup pitchGroup) {
		return counter.getOrDefault(group, Collections.emptyMap())
			.getOrDefault(pitchGroup, Collections.emptySet());
	}

	public static PitchGroup getGroupFromPitch(float pitch) {
		if (pitch < .70)
			return PitchGroup.VERY_LOW;
		if (pitch < .90)
			return PitchGroup.LOW;
		if (pitch < 1.10)
			return PitchGroup.NORMAL;
		if (pitch < 1.30)
			return PitchGroup.HIGH;
		return PitchGroup.VERY_HIGH;
	}
}