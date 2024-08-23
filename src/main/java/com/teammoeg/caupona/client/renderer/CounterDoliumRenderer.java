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
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CounterDoliumRenderer implements BlockEntityRenderer<CounterDoliumBlockEntity> {

	/**
	 * @param rendererDispatcherIn  
	 */
	public CounterDoliumRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}



	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(CounterDoliumBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if (!CPBlocks.dolium.contains(state.getBlock()))
			return;

		if (blockEntity.tank.isEmpty())
			return;
		FluidStack fs = blockEntity.tank.getFluid();
		matrixStack.pushPose();
		if (fs != null && !fs.isEmpty() && fs.getFluid() != null) {
			float rr = (fs.getAmount() / 1250f) * 0.5f + 0.375f;
			matrixStack.translate(0, rr, 0);
			matrixStack.mulPose(GuiUtils.rotate90);

			VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
			IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(fs.getFluid());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(attr.getStillTexture(fs));
			int col = attr.getTintColor(fs);
			float alp = 1f;
			GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f,
				(col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, alp,
				sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
					combinedOverlayIn);

			
		}

		matrixStack.popPose();
	}

}