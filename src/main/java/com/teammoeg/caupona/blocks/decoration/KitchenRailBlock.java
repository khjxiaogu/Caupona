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

package com.teammoeg.caupona.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KitchenRailBlock extends Block {
	protected static final VoxelShape OCTET_N = Block.box(0.0D , 0.0D, 0.0D , 16.0D, 16.0D, 8.0D );
	protected static final VoxelShape OCTET_W = Block.box(0.0D , 0.0D, 0.0D , 8.0D , 16.0D, 16.0D);
	protected static final VoxelShape OCTET_E = Block.box(8.0D, 0.0D, 0.0D , 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape OCTET_S = Block.box(0.0D , 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	public KitchenRailBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
		case NORTH:return OCTET_N;
		case SOUTH:return OCTET_S;
		case WEST:return OCTET_W;
		case EAST:return OCTET_E;
		default:return super.getShape(state, level, pos, context);
		}
	}
	@Override
	public boolean isPathfindable(BlockState pState,PathComputationType pType) {
	      return false;
	}
}
