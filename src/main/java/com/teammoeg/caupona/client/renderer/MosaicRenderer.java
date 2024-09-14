/*
 * Copyright (c) 2024 TeamMoeg
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

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.MosaicData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MosaicRenderer extends BlockEntityWithoutLevelRenderer {
	//ResourceLocation name=new ResourceLocation("block/block");
	public MosaicRenderer() {
		super(null,null);
	}
	//private static DynamicBlockModelReference model=ModelUtils.getModel("item_block");
	@SuppressWarnings("deprecation")
	@Override
	public void renderByItem(ItemStack is, ItemDisplayContext ctx, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		@Nullable MosaicData tag=is.get(CPCapability.MOSAIC_DATA);
		if(tag==null)
			return;
		
		BlockState bs=tag.createBlock();
		BlockRenderDispatcher rd = Minecraft.getInstance().getBlockRenderer();
		//matrixStack.translate(1F, 0, 0);
		//model.get()
		//.applyTransform(ctx, matrixStack, ctx==ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
		//BlockRenderDispatcher rdr = Minecraft.getInstance().getBlockRenderer();
		//BakedModel bakedmodel = rdr.getBlockModel(bs);
		//bakedmodel.applyTransform(ctx, matrixStack, false);
       // for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(pState, RandomSource.create(42), modelData))
         //  rdr.getModelRenderer().renderModel(pPoseStack.last(), pBufferSource.getBuffer(renderType != null ? renderType : net.neoforged.neoforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), bs, bakedmodel, 1f, 1f, 1f,combinedLightIn, combinedOverlayIn, modelData, rt);
		BakedModel bm=rd.getBlockModel(bs);
		ItemRenderer ir=Minecraft.getInstance().getItemRenderer();
		VertexConsumer pBuffer = buffer.getBuffer(RenderType.cutout());
		RandomSource randomsource = RandomSource.create();
		//Lighting.setupForEntityInInventory();;
		for (Direction direction : Direction.values()) {
			randomsource.setSeed(42L);
			ir.renderQuadList(matrixStack, pBuffer, bm.getQuads(bs, direction, randomsource), is, combinedLightIn,
					combinedOverlayIn);
		}

		randomsource.setSeed(42L);
		ir.renderQuadList(matrixStack, pBuffer, bm.getQuads(bs, (Direction) null, randomsource), is, combinedLightIn,
				combinedOverlayIn);
		//this.renderByItem(is, ctx, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
		//rd.renderSingleBlock(bs, matrixStack, buffer, combinedLightIn, combinedOverlayIn,ModelData.builder().build(),RenderType.cutout());
	}

}
