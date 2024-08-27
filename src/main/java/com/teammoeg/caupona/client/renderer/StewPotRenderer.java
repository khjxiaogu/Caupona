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

package com.teammoeg.caupona.client.renderer;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.blocks.pot.StewPot;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class StewPotRenderer implements BlockEntityRenderer<StewPotBlockEntity> {

	/**
	 * @param rendererDispatcherIn
	 */
	public StewPotRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	private static Vector3f clr(int fromcol, int tocol, float proc) {
		float fcolr = (fromcol >> 16 & 255) / 255.0f, fcolg = (fromcol >> 8 & 255) / 255.0f,
				fcolb = (fromcol & 255) / 255.0f, tcolr = (tocol >> 16 & 255) / 255.0f,
				tcolg = (tocol >> 8 & 255) / 255.0f, tcolb = (tocol & 255) / 255.0f;
		return new Vector3f(fcolr + (tcolr - fcolr) * proc, fcolg + (tcolg - fcolg) * proc,
				fcolb + (tcolb - fcolb) * proc);
	}

	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(StewPotBlockEntity blockEntity, float partialTicks, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if (!(state.getBlock() instanceof StewPot))
			return;
		matrixStack.pushPose();
		FluidStack fs = blockEntity.getTank().getFluid();
		if (fs != null && !fs.isEmpty() && fs.getFluid() != null) {
			float rr = fs.getAmount();
			if (blockEntity.proctype == 2)// just animate fluid reduction
				rr += 250f * (1 - blockEntity.process * 1f / blockEntity.processMax);
			float yy = Math.min(1, rr / blockEntity.getTank().getCapacity()) * .5f + .1875f;
			matrixStack.translate(0, yy, 0);
			matrixStack.mulPose(GuiUtils.rotate90);
			VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
			IClientFluidTypeExtensions attr0 = IClientFluidTypeExtensions.of(fs.getFluid());
			TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
			TextureAtlasSprite sprite = atlas.getSprite(attr0.getStillTexture(fs));
			int col = attr0.getTintColor(fs);
			Vector3f clr;
			float alp = 1f;
			if (blockEntity.output != null&&!blockEntity.output.isEmpty() && blockEntity.processMax > 0) {
				IClientFluidTypeExtensions attr1 = IClientFluidTypeExtensions.of(blockEntity.output.getFluid());
				TextureAtlasSprite sprite2 = atlas.getSprite(attr1.getStillTexture(fs));
				float proc = blockEntity.process * 1f / blockEntity.processMax;
				clr = clr(col, attr1.getTintColor(fs), proc);

				alp = 1 - proc;
				GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
						clr.z(), proc, sprite2.getU0(), sprite2.getU1(), sprite2.getV0(), sprite2.getV1(),
						combinedLightIn, combinedOverlayIn);

			} else {
				clr = clr(col);

			}
			GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
					clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
					combinedOverlayIn);

		}

		matrixStack.popPose();
	}

}