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

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.blocks.pan.PanBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.client.util.ModelUtils;
import com.teammoeg.caupona.item.DishItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class PanRenderer implements BlockEntityRenderer<PanBlockEntity> {

	/**
	 * @param rendererDispatcherIn  
	 */
	public PanRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}


	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(PanBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		Block b = state.getBlock();
		if(!(b instanceof PanBlock))return;
		Item torender = null;
		if (!blockEntity.sout.isEmpty()) {
			torender = blockEntity.sout.getItem();
		} else if (!blockEntity.preout.isEmpty()) {
			torender = blockEntity.preout.getItem();
		} else {
			ItemStack is = blockEntity.inv.getStackInSlot(10);
			if (!is.isEmpty())
				torender = is.getItem();
		}
		if (!(torender instanceof DishItem))
			return;
		BlockState bs = ((DishItem) torender).bl.defaultBlockState();
		if (b == CPBlocks.STONE_PAN.get()) {
			bs = bs.setValue(DishBlock.PAN, 1);
		} else
			bs = bs.setValue(DishBlock.PAN, 2);
		BlockRenderDispatcher rd = Minecraft.getInstance().getBlockRenderer();
		BakedModel bm=rd.getBlockModel(bs);
		ModelData imd = bm.getModelData(blockEntity.getLevel(), blockEntity.getBlockPos(), bs,
				blockEntity.getLevel().getModelDataManager().getAt((blockEntity.getBlockPos())));
		if(imd==null)return;
		ModelUtils.tesellate(blockEntity,bs,bm, buffer.getBuffer(RenderType.cutout()), matrixStack, combinedOverlayIn,imd);
		//rd.renderSingleBlock(bs, matrixStack, buffer, combinedLightIn, combinedOverlayIn, imd,RenderType.cutout());

	}

}