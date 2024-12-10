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

package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.components.MosaicData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MosaicBlock extends CPHorizontalBlock{
	public static final EnumProperty<MosaicMaterial> MATERIAL_1=EnumProperty.create("material_1", MosaicMaterial.class);
	
	public static final EnumProperty<MosaicMaterial> MATERIAL_2=EnumProperty.create("material_2", MosaicMaterial.class);
	@SuppressWarnings("unchecked")
	public static final EnumProperty<MosaicMaterial>[] MATERIAL=new EnumProperty[] {MATERIAL_1,MATERIAL_2};
	public static final EnumProperty<MosaicPattern> PATTERN=EnumProperty.create("pattern", MosaicPattern.class);
	public MosaicBlock(Properties pProperties) {
		super(pProperties);
		CODEC = simpleCodec(MosaicBlock::new);
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(MATERIAL_1).add(MATERIAL_2).add(PATTERN);
	}
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState bs=super.getStateForPlacement(pContext);
		@Nullable MosaicData tag=pContext.getItemInHand().get(CPCapability.MOSAIC_DATA);
		if(tag==null)
			return bs;
		return tag.createBlock(bs);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state,
			net.minecraft.world.level.storage.loot.LootParams.Builder p_287596_) {
		ItemStack is=new ItemStack(CPBlocks.MOSAIC.get());
		MosaicItem.setMosaic(is,state.getValue(MATERIAL_1),state.getValue(MATERIAL_2),state.getValue(PATTERN));
		List<ItemStack> ret=new ArrayList<>();
		ret.add(is);
		return ret;
	}
	@Override
	public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState state) {
		ItemStack is=new ItemStack(CPBlocks.MOSAIC.get());
		MosaicItem.setMosaic(is,state.getValue(MATERIAL_1),state.getValue(MATERIAL_2),state.getValue(PATTERN));
		return is;
	}

}
