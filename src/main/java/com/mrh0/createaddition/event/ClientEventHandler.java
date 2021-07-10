package com.mrh0.createaddition.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.rendering.WireNodeRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateAddition.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void playerRendererEvent(RenderWorldLastEvent evt) {
		MatrixStack matrixStackIn = evt.getMatrixStack();
		IRenderTypeBuffer bufferIn = Minecraft.getInstance().getBufferBuilders().getOutlineVertexConsumers();//evt.getBuffers();
		ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);//evt.getItemStack();
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
		
		World world = Minecraft.getInstance().world;
		
		
		TileEntity te = world.getTileEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof IWireNode))
			return;
		
		IWireNode wn = (IWireNode) te;
		
		
		ClientPlayerEntity p = Minecraft.getInstance().player;
		
		float doubleX = (float) (p.lastTickPosX + (p.getPositionVec().getX() - p.lastTickPosX) * evt.getPartialTicks()); 
		float doubleY = (float) (p.lastTickPosY + (p.getPositionVec().getY() - p.lastTickPosY) * evt.getPartialTicks()); 
		float doubleZ = (float) (p.lastTickPosZ + (p.getPositionVec().getZ() - p.lastTickPosZ) * evt.getPartialTicks()); 
		
		float tx = te.getPos().getX() + wn.getNodeOffset(node).getX() + 0.5f;
		float ty = te.getPos().getY() + wn.getNodeOffset(node).getY() - 1f;
		float tz = te.getPos().getZ() + wn.getNodeOffset(node).getZ() + 0.5f;

		matrixStackIn.push();

		// IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getLines());
		// Matrix4f matrix4f1 = matrixStackIn.peek().getModel();

		float dis = WireNodeRenderer.distanceFromZero(-doubleX + tx, -doubleY + ty, -doubleZ + tz);
		if(dis > IWireNode.MAX_LENGTH)
			return;

		//matrixStackIn.translate(tx + .5f, ty + .5f, tz + .5f);
		matrixStackIn.translate(0,-0.2f,0);
		WireNodeRenderer.wireRender(te, p.getBlockPos(), matrixStackIn, bufferIn, -doubleX + tx, -doubleY + ty, -doubleZ + tz, WireSpool.getWireType(stack.getItem()), dis);
		matrixStackIn.pop();
	}
	
	@SubscribeEvent
	public static void getFogDensity(EntityViewRenderEvent.FogDensity event) {
		ActiveRenderInfo info = event.getInfo();
		FluidState fluidState = info.getFluidState();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getFluid();

		if (fluid.isEquivalentTo(CAFluids.SEED_OIL.get())) {
			event.setDensity(3.5f);
			event.setCanceled(true);
			return;
		}
	}

	@SubscribeEvent
	public static void getFogColor(EntityViewRenderEvent.FogColors event) {
		ActiveRenderInfo info = event.getInfo();
		FluidState fluidState = info.getFluidState();
		if (fluidState.isEmpty())
			return;
		Fluid fluid = fluidState.getFluid();

		if (fluid.isEquivalentTo(CAFluids.SEED_OIL.get())) {
			event.setRed(70 / 256f);
			event.setGreen(74 / 256f);
			event.setBlue(52 / 256f);
		}
	}
}