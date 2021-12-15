package com.mrh0.createaddition.event;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.rendering.WireNodeRenderer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateAddition.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
	
	// Old Wiring renderer
	/*
	@SubscribeEvent
	public static void playerRendererEvent(RenderWorldLastEvent evt) {
		MatrixStack matrixStackIn = evt.getMatrixStack();
		IRenderTypeBuffer bufferIn = Minecraft.getInstance().renderBuffers().bufferSource();//.outlineBufferSource();//evt.getBuffers();
		ItemStack stack = Minecraft.getInstance().player.getItemInHand(Hand.MAIN_HAND);//evt.getItemStack();
		if(stack.isEmpty())
			return;
		if(!(stack.getItem() instanceof WireSpool))
			return;
		if(WireSpool.isRemover(stack.getItem()))
			return;
		if(!WireSpool.hasPos(stack.getTag()))
			return;
		BlockPos pos = WireSpool.getPos(stack.getTag());
		int node = WireSpool.getNode(stack.getTag());
		
		World world = Minecraft.getInstance().level;
		
		
		TileEntity te = world.getBlockEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof IWireNode))
			return;
		
		IWireNode wn = (IWireNode) te;
		
		
		ClientPlayerEntity p = Minecraft.getInstance().player;
		
		float doubleX = (float) (p.xOld + (p.position().x() - p.xOld) * evt.getPartialTicks()); 
		float doubleY = (float) (p.yOld + (p.position().y() - p.yOld) * evt.getPartialTicks()); 
		float doubleZ = (float) (p.zOld + (p.position().z() - p.zOld) * evt.getPartialTicks()); 
		
		float tx = te.getBlockPos().getX() + wn.getNodeOffset(node).x() + 0.5f;
		float ty = te.getBlockPos().getY() + wn.getNodeOffset(node).y() - 1f;
		float tz = te.getBlockPos().getZ() + wn.getNodeOffset(node).z() + 0.5f;

		//matrixStackIn.pushPose();

		// IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getLines());
		// Matrix4f matrix4f1 = matrixStackIn.peek().getModel();

		float dis = WireNodeRenderer.distanceFromZero(-doubleX + tx, -doubleY + ty, -doubleZ + tz);
		if(dis > IWireNode.MAX_LENGTH)
			return;

		//matrixStackIn.translate(tx + .5f, ty + .5f, tz + .5f);
		matrixStackIn.translate(0,-0.2f,0);
		WireNodeRenderer.wireRender(te, p.blockPosition(), matrixStackIn, bufferIn, -doubleX + tx, -doubleY + ty, -doubleZ + tz, WireSpool.getWireType(stack.getItem()), dis);
		//matrixStackIn.popPose();
	}
	*/
	
	// Fluid Fog TODO: update!
	/*@SubscribeEvent
	public static void getFogDensity(EntityViewRenderEvent.FogDensity event) {
		Camera info = event.getInfo();
		FluidState fluidState = info.getFluidInCamera();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getType();

		if (fluid.isSame(CAFluids.SEED_OIL.get())) {
			event.setDensity(3.5f);
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void getFogColor(EntityViewRenderEvent.FogColors event) {
		Camera info = event.getInfo();
		FluidState fluidState = info.getFluidInCamera();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getType();

		if (fluid.isSame(CAFluids.SEED_OIL.get())) {
			event.setRed(70 / 256f);
			event.setGreen(74 / 256f);
			event.setBlue(52 / 256f);
		}
	}*/
}