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

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.pan.PanBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.client.util.DisplayGroupProperty;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class PanRenderer implements BlockEntityRenderer<PanBlockEntity> {

	/**
	 * @param rendererDispatcherIn  
	 */
	public PanRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	ModelData panLayer=ModelData.builder().with(DisplayGroupProperty.PROPERTY, ImmutableSet.of("ServingsInPan")).build();
	ModelData plateLayer=ModelData.builder().with(DisplayGroupProperty.PROPERTY, ImmutableSet.of("ServingsOnPlate")).build();

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(PanBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		Block b = state.getBlock();
		if(!(b instanceof PanBlock))return;
		ResourceLocation torender = blockEntity.model;
		if(torender==null)
			return;
		DynamicBlockModelReference model=DynamicBlockModelReference.getModelCached(torender);


		ModelData imd;
		if((b == CPBlocks.STONE_PAN.get()))
			imd=plateLayer;
		else
			imd=panLayer;
		if(imd==null)return;
		ModelUtils.tesellate(blockEntity, model, buffer.getBuffer(RenderType.CUTOUT), matrixStack, combinedOverlayIn, imd);

	}

}