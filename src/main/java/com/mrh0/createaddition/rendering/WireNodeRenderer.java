package com.mrh0.createaddition.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.event.ClientEventHandler;
import com.mrh0.createaddition.index.CAPartials;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class WireNodeRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {//extends BlockEntityRenderer<T> {
	public WireNodeRenderer(BlockEntityRendererProvider.Context context) {
		super();
	}

	private static final float HANG = 0.5f;
	private float time = 0f;

	@Override
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		IWireNode te = (IWireNode) tileEntityIn;

		time += partialTicks;

		for (int i = 0; i < te.getNodeCount(); i++) {
			if (!te.hasConnection(i)) continue;
			Vec3 d1 = te.getNodeOffset(i);
			float ox1 = ((float) d1.x());
			float oy1 = ((float) d1.y());
			float oz1 = ((float) d1.z());

			IWireNode wn = te.getWireNode(i);
			if (wn == null) return;

				Vec3 d2 = wn.getNodeOffset(te.getOtherNodeIndex(i)); // get other
				float ox2 = ((float) d2.x());
				float oy2 = ((float) d2.y());
				float oz2 = ((float) d2.z());
				BlockPos other = te.getNodePos(i);

				float tx = other.getX() - te.getPos().getX();
				float ty = other.getY() - te.getPos().getY();
				float tz = other.getZ() - te.getPos().getZ();
				matrixStackIn.pushPose();

				float dis = distanceFromZero(tx, ty, tz);

				matrixStackIn.translate(tx + .5f + ox2, ty + .5f + oy2, tz + .5f + oz2);
				wireRender(
						tileEntityIn,
						other,
						matrixStackIn,
						bufferIn,
						-tx - ox2 + ox1,
						-ty - oy2 + oy1,
						-tz - oz2 + oz1,
						te.getNodeType(i),
						dis
				);
				matrixStackIn.popPose();
			}

		if(ClientEventHandler.clientRenderHeldWire) {
			LocalPlayer player = ClientMinecraftWrapper.getPlayer();
			Util.Triple<BlockPos, Integer, WireType> wireNode = Util.getWireNodeOfSpools(player.getInventory().getSelected());
			if(wireNode == null) return;

			BlockPos nodePos = wireNode.a;
			int nodeIndex = wireNode.b;
			WireType wireType = wireNode.c;
			if(!nodePos.equals(te.getPos())) return;

			Vec3 d1 = te.getNodeOffset(nodeIndex);
			float ox1 = ((float) d1.x());
			float oy1 = ((float) d1.y());
			float oz1 = ((float) d1.z());

			Vec3 playerPos = player.getPosition(partialTicks);
			float tx = (float)playerPos.x - te.getPos().getX();
			float ty = (float)playerPos.y - te.getPos().getY();
			float tz = (float)playerPos.z - te.getPos().getZ();
			matrixStackIn.pushPose();

			float dis = distanceFromZero(tx, ty, tz);

			matrixStackIn.translate(tx + .5f, ty + .5f, tz + .5f);
			wireRender(
					tileEntityIn,
					player.blockPosition(),
					matrixStackIn,
					bufferIn,
					-tx + ox1,
					-ty + oy1,
					-tz + oz1,
					wireType,
					dis
			);
			matrixStackIn.popPose();
		}
	}



	private static float divf(int a, int b) {
		return (float) a / (float) b;
	}

	private static float hang(float f, float dis) {
		return (float) Math.sin(-f * (float) Math.PI) * (HANG * dis / (float) Config.SMALL_CONNECTOR_MAX_LENGTH.get());
	}

	public static float distanceFromZero(float x, float y, float z) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public static void wireRender(BlockEntity tileEntityIn, BlockPos other, PoseStack stack, MultiBufferSource buffer, float x, float y, float z,
			WireType type, float dis) {
		//matrix.pushPose();

		VertexConsumer ivertexbuilder = buffer.getBuffer(CARenderType.WIRE);
		Matrix4f matrix4f = stack.last().pose();
		float f = (float) (Mth.fastInvSqrt(x * x + z * z) * 0.025F / 2.0F);
		float o1 = z * f;
		float o2 = x * f;
		BlockPos blockpos1 = tileEntityIn.getBlockPos();
		BlockPos blockpos2 = other;

		int i = tileEntityIn.getLevel().getBrightness(LightLayer.BLOCK, blockpos1);
		int j = tileEntityIn.getLevel().getBrightness(LightLayer.BLOCK, blockpos2);
		int k = tileEntityIn.getLevel().getBrightness(LightLayer.SKY, blockpos1);
		int l = tileEntityIn.getLevel().getBrightness(LightLayer.SKY, blockpos2);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.025F, o1, o2, type, dis, tileEntityIn.getBlockState(), stack, 0, 1f);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.0F, o1, o2, type, dis, tileEntityIn.getBlockState(), stack, 1, 1f);
		//light
		//matrix.popPose();
	}

	public static void wirePart(VertexConsumer vertBuilder, Matrix4f matrix, float x, float y, float z, int l1, int l2,
			int l3, int l4, float a, float b, float o1, float o2, WireType type, float dis, BlockState state, PoseStack stack, int lightOffset, float hangFactor) {
		for (int j = 0; j < 24; ++j) {
			float f = (float) j / 23.0F;
			int k = (int) Mth.lerp(f, (float) l1, (float) l2);
			int l = (int) Mth.lerp(f, (float) l3, (float) l4);
			int light = LightTexture.pack(k, l);

			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j, false, o1, o2, type, dis, state, stack, lightOffset, hangFactor);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j + 1, true, o1, o2, type, dis, state, stack, lightOffset+1, hangFactor);

		}

		if (type.isFestive()) {
			stack.pushPose();
			boolean main = x + y + z > 0;
			for (int j = 0; j < 24; ++j) {
				lights(vertBuilder, x, y, z, a, b, 24, j + 1, o1, o2, type, dis, state, stack, lightOffset, main);
			}
			stack.popPose();
		}
	}

	static Color[] colors = {Color.RED, Color.GREEN, new Color(0f, 0f, 1f, 1f)};
	static float LIGHT_Y_OFFSET = -0.03f;

	public static void lights(VertexConsumer vertBuilder,float x, float y, float z,
			float a, float b, int count, int index, float o1, float o2, WireType type, float dis, BlockState state, PoseStack stack, int lightOffset, boolean main) {
		float part = (float) index / (float) count;
		float fx = x * part;
		float fy = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part)) + hang(divf(index, count), dis) + LIGHT_Y_OFFSET;
		float fz = z * part;

		if (index % 3 == 0 && index != 1 && index != count && lightOffset == 0) {
			CachedBufferer.partial(CAPartials.SMALL_LIGHT, state).color(colors[(main ? 2-(index/3)%3 : (index/3)%3)]).light(255).translate(fx, fy, fz).renderInto(stack, vertBuilder);
		}
	}

	public static void supports(VertexConsumer vertBuilder,float x, float y, float z,
			float a, float b, int count, int index, float o1, float o2, WireType type, float dis, BlockState state, PoseStack stack, int lightOffset, boolean main) {
		float part = (float) index / (float) count;
		float fx = x * part;
		float fyh = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part)) + 2*hang(divf(index, count), dis);
		float fy = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part));
		float fz = z * part;

		if (index % 3 == 0 && index != 1 && index != count && lightOffset == 0) {
			float l = 1.7f*16f-fyh*16f;
			CachedBufferer.partial(CAPartials.SMALL_LIGHT, state).light(255).translate(fx, fy + hang(divf(index, count), dis), fz).scale(.5f, l, .5f).renderInto(stack, vertBuilder);//.scale(.25f, (1.7f*16f)-fy, .25f)
		}
	}

	public static void wireVert(VertexConsumer vertBuilder, Matrix4f matrix, int light, float x, float y, float z,
			float a, float b, int count, int index, boolean sw, float o1, float o2, WireType type, float dis, BlockState state, PoseStack stack, int lightOffset, float hangFactor) {
		int cr = type.getRed();
		int cg = type.getGreen();
		int cb = type.getBlue();
		if (index % 2 == 0) {
			cr *= 0.7F;
			cg *= 0.7F;
			cb *= 0.7F;
		}

		float part = (float) index / (float) count;
		float fx = x * part;
		float fy = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part)) + (hangFactor*hang(divf(index, count), dis));
		float fz = z * part;

		//System.out.println((fx + o1) +":"+ (fy + n1 - n2) +":"+ (fz - o2));


		if(Math.abs(x) + Math.abs(z) < Math.abs(y)) {
			boolean p = b > 0;
			float c = 0.015f;

			if (!sw) {
				vertBuilder.vertex(matrix, fx -c, fy, fz + (p?-c:c)).color(cr, cg, cb, 255).uv2(light).endVertex();
			}

			vertBuilder.vertex(matrix, fx + c, fy, fz + (p?c:-c)).color(cr, cg, cb, 255).uv2(light).endVertex();
			if (sw) {
				vertBuilder.vertex(matrix, fx -c, fy, fz + (p?-c:c)).color(cr, cg, cb, 255).uv2(light).endVertex();
			}
		}
		else {
			if (!sw) {
				vertBuilder.vertex(matrix, fx + o1, fy + a - b, fz - o2).color(cr, cg, cb, 255).uv2(light).endVertex();
			}

			vertBuilder.vertex(matrix, fx - o1, fy + b, fz + o2).color(cr, cg, cb, 255).uv2(light).endVertex();
			if (sw) {
				vertBuilder.vertex(matrix, fx + o1, fy + a - b, fz - o2).color(cr, cg, cb, 255).uv2(light).endVertex();
			}
		}
	}
}
