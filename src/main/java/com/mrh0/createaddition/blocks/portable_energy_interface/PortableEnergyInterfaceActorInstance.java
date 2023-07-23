package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;

public class PortableEnergyInterfaceActorInstance extends ActorInstance {

	private final PortableEnergyInterfaceInstance.PIInstance instance;

	public PortableEnergyInterfaceActorInstance(MaterialManager materialManager, VirtualRenderWorld world, MovementContext context) {
		super(materialManager, world, context);
		this.instance = new PortableEnergyInterfaceInstance.PIInstance(materialManager, context.state, context.localPos);
		this.instance.init(false);
		this.instance.middle.setBlockLight(this.localBlockLight());
		this.instance.top.setBlockLight(this.localBlockLight());
	}

	public void beginFrame() {
		LerpedFloat lf = PortableStorageInterfaceMovement.getAnimation(this.context);
		this.instance.tick(lf.settled());
		this.instance.beginFrame(lf.getValue(AnimationTickHolder.getPartialTicks()));
	}

}
