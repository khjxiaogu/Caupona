/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client.util;

import java.util.function.Function;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

/**
 * Fluid render codes adapted from Immersive Engineering and modified.
 * Related codes fall under their license and open-sourced.
 * 
 * @author BluSunrize
 * @author khjxiaogu
 */
public class GuiUtils {

	private GuiUtils() {
	}
	public static Quaternionf rotate90=new Quaternionf(new AxisAngle4f((float) (Math.PI/2),1,0,0));
	public static void handleGuiTank(GuiGraphics transform, IFluidTank tank, int x, int y, int w, int h) {
		FluidStack fluid = tank.getFluid();
		
		BufferSource buffer=transform.bufferSource();
		if (fluid != null && fluid.getFluid() != null) {
			int fluidHeight = (int) (h * (fluid.getAmount() / (float) tank.getCapacity()));
			drawRepeatedFluidSpriteGui(buffer, transform.pose(), fluid, x, y + h - fluidHeight, w, fluidHeight);
		}
		buffer.endBatch();
	}

	private static final Function<ResourceLocation, RenderType> GUI_CUTOUT = Util
			.memoize(texture -> RenderType.create("gui_" + texture, 
		        DefaultVertexFormat.BLOCK,
		        VertexFormat.Mode.QUADS,
		        786432,
		        true,
		        false,
		        RenderType.CompositeState.builder()
		            .setLightmapState(RenderType.LIGHTMAP)
		            .setShaderState(RenderType.RENDERTYPE_CUTOUT_SHADER)
		            .setTextureState(RenderType.BLOCK_SHEET)
		            .createCompositeState(true)
		    ));

	private static void buildVertex(VertexConsumer bu, PoseStack transform, float r, float g, float b, float a,
			float p1, float p2, float u0, float u1, int light, int overlay) {
		bu.addVertex(transform.last().pose(), p1, p2, 0).setColor(r, g, b, a).setUv(u0, u1).setOverlay(overlay).setLight(light)
				.setNormal(1f, 1f, 1f);
	}

	public static void drawRepeatedFluidSpriteGui(MultiBufferSource.BufferSource buffer, PoseStack transform,
			FluidStack fluid, float x, float y, float w, float h) {
		RenderType renderType = GUI_CUTOUT.apply(InventoryMenu.BLOCK_ATLAS);
		VertexConsumer builder = buffer.getBuffer(renderType);
		IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(fluid.getFluid());
		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
				.getSprite(attr.getStillTexture(fluid));
		int col = attr.getTintColor(fluid);
		float alpha=(col >> 16 & 255)/255f*.8f;
		if(alpha<0.001)alpha=1;
		
		drawRepeatedSprite(builder, transform, x, y, w, h,16,16, sprite.getU0(), sprite.getU1(), sprite.getV0(),
				sprite.getV1(), (col >> 16 & 255) / 255f, (col >> 8 & 255) / 255f, (col & 255) / 255f, alpha,
				LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
		buffer.endBatch(renderType);
	}

	public static void drawRepeatedSprite(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax, float r, float g,
			float b, float alpha, int light, int overlay) {
		int iterMaxW = (int) (w / iconWidth);
		int iterMaxH = (int) (h / iconHeight);
		float leftoverW = w % iconWidth;
		float leftoverH = h % iconHeight;
		float leftoverWf = leftoverW / iconWidth;
		float leftoverHf = leftoverH / iconHeight;
		float iconUDif = uMax - uMin;
		float iconVDif = vMax - vMin;
		for (int ww = 0; ww < iterMaxW; ww++) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + hh * iconHeight, iconWidth,
						iconHeight, r, g, b, alpha, uMin, uMax, vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + iterMaxH * iconHeight, iconWidth,
					leftoverH, r, g, b, alpha, uMin, uMax, vMin, (vMin + iconVDif * leftoverHf), light, overlay);
		}
		if (leftoverW > 0) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + hh * iconHeight, leftoverW,
						iconHeight, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + iterMaxH * iconHeight, leftoverW,
					leftoverH, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin,
					(vMin + iconVDif * leftoverHf), light, overlay);
		}
	}

	public static void drawTexturedColoredRect(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, float r, float g, float b, float alpha, float u0, float u1, float v0, float v1, int light,
			int overlay) {
		buildVertex(builder, transform, r, g, b, alpha, x, y + h, u0, v1, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x + w, y + h, u1, v1, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x + w, y, u1, v0, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x, y, u0, v0, light, overlay);
	}
}
