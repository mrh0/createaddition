package com.mrh0.createaddition.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;

public class WireNodeRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

	public WireNodeRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private static final float HANG = 0.5f;

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combindOverlayIn) {
		IWireNode te = (IWireNode) tileEntityIn;

		for (int i = 0; i < te.getNodeCount(); i++) {
			if (te.getNodeType(i) != null) {
				Vector3f d1 = te.getNodeOffset(i);
				float ox1 = ((float) d1.x());
				float oy1 = ((float) d1.y());
				float oz1 = ((float) d1.z());

				IWireNode wn = te.getNode(i);
				if (wn == null)
					return;

				Vector3f d2 = wn.getNodeOffset(te.getOtherNodeIndex(i)); // get other
				float ox2 = ((float) d2.x());
				float oy2 = ((float) d2.y());
				float oz2 = ((float) d2.z());

				BlockPos other = te.getNodePos(i);
				
				float tx = other.getX() - te.getMyPos().getX();
				float ty = other.getY() - te.getMyPos().getY();
				float tz = other.getZ() - te.getMyPos().getZ();

				matrixStackIn.pushPose();

				// IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getLines());
				// Matrix4f matrix4f1 = matrixStackIn.peek().getModel();

				float dis = distanceFromZero(tx, ty, tz);

				matrixStackIn.translate(tx + .5f + ox2, ty + .5f + oy2, tz + .5f + oz2);
				wireRender(tileEntityIn, other, matrixStackIn, bufferIn, -tx - ox2 + ox1, -ty - oy2 + oy1, -tz - oz2 + oz1,
						te.getNodeType(i), dis);

				/*
				 * float dis = distanceFromZero(tx, ty, tz);
				 * 
				 * if(ty+(oy2-oy1) < 0) { matrixStackIn.translate(tx+.5f + ox2, ty+.25f + oy2,
				 * tz+.5f + oz2);
				 * 
				 * for(int k = 0; k < 16; ++k) { vert(-tx - ox2 + ox1, -ty - oy2 + oy1, -tz -
				 * oz2 + oz1, ivertexbuilder1, matrix4f1, divf(k, 16), type, dis); vert(-tx -
				 * ox2 + ox1, -ty - oy2 + oy1, -tz - oz2 + oz1, ivertexbuilder1, matrix4f1,
				 * divf(k + 1, 16), type, dis);//-tx, -ty, -tz, } } else {
				 * matrixStackIn.translate(.5f + ox1, .25f + oy1, .5f + oz1);
				 * 
				 * for(int k = 0; k < 16; ++k) { vert(tx - ox1 + ox2, ty - oy1 + oy2, tz - oz1 +
				 * oz2, ivertexbuilder1, matrix4f1, divf(k, 16), type, dis); vert(tx - ox1 +
				 * ox2, ty - oy1 + oy2, tz - oz1 + oz2, ivertexbuilder1, matrix4f1, divf(k + 1,
				 * 16), type, dis);//tx, ty, tz, } }
				 */

				matrixStackIn.popPose();
			}
		}
	}

	private static float divf(int a, int b) {
		return (float) a / (float) b;
	}

	/*
	 * private static void vert(float x, float y, float z, IVertexBuilder builder,
	 * Matrix4f matrix, float f, WireType type, float dis) { builder.vertex(matrix,
	 * x * f, y * (f * f + f) * 0.5F + 0.25F + hang(f, dis), z *
	 * f).color(type.getRed(), type.getGreen(), type.getBlue(), 255).endVertex(); }
	 */

	private static float hang(float f, float dis) {
		return (float) Math.sin(-f * (float) Math.PI) * (HANG * dis / (float) IWireNode.MAX_LENGTH);
	}

	public static float distanceFromZero(float x, float y, float z) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public static void wireRender(TileEntity tileEntityIn, BlockPos other, MatrixStack matrix, IRenderTypeBuffer buffer, float x, float y, float z,
			WireType type, float dis) {
		matrix.pushPose();

		// matrix.translate(tileEntityIn.getPos().getX(), tileEntityIn.getPos().getY(),
		// tileEntityIn.getPos().getZ());
		// float f = (float) 1;
		// float f1 = (float) 1;
		// float f2 = (float) 1;
		IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.leash());
		Matrix4f matrix4f = matrix.last().pose();
		float f = MathHelper.fastInvSqrt(x * x + z * z) * 0.025F / 2.0F;
		float o1 = z * f;
		float o2 = x * f;
		BlockPos blockpos1 = tileEntityIn.getBlockPos();//new BlockPos(tileEntityIn.getPos());
		BlockPos blockpos2 = other;//new BlockPos(blockpos1.getX() + x, blockpos1.getY() + y, blockpos1.getZ() + z);
		//System.out.println("Pos:" + blockpos1 + ":" + blockpos2);
		int i = tileEntityIn.getLevel().getBrightness(LightType.BLOCK, blockpos1);
		int j = tileEntityIn.getLevel().getBrightness(LightType.BLOCK, blockpos2);
		int k = tileEntityIn.getLevel().getBrightness(LightType.SKY, blockpos1);
		int l = tileEntityIn.getLevel().getBrightness(LightType.SKY, blockpos2);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.025F, o1, o2, type, dis);
		wirePart(ivertexbuilder, matrix4f, x, y, z, j, i, l, k, 0.025F, 0.0F, o1, o2, type, dis);
		matrix.popPose();
	}

	public static void wirePart(IVertexBuilder vertBuilder, Matrix4f matrix, float x, float y, float z, int l1, int l2,
			int l3, int l4, float a, float b, float o1, float o2, WireType type, float dis) {
		for (int j = 0; j < 24; ++j) {
			float f = (float) j / 23.0F;
			int k = (int) MathHelper.lerp(f, (float) l1, (float) l2);
			int l = (int) MathHelper.lerp(f, (float) l3, (float) l4);
			int light = LightTexture.pack(k, l);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j, false, o1, o2, type, dis);
			wireVert(vertBuilder, matrix, light, x, y, z, a, b, 24, j + 1, true, o1, o2, type, dis);
		}
	}

	public static void wireVert(IVertexBuilder vertBuilder, Matrix4f matrix, int light, float x, float y, float z,
			float a, float b, int count, int index, boolean sw, float o1, float o2, WireType type, float dis) {
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
		float fy = (y > 0.0F ? y * part * part : y - y * (1.0F - part) * (1.0F - part)) + hang(divf(index, count), dis);
		float fz = z * part;

		//System.out.println((fx + o1) +":"+ (fy + n1 - n2) +":"+ (fz - o2));
		
		if(Math.abs(x) + Math.abs(z) < 2f && Math.abs(y) > 2f) {
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
