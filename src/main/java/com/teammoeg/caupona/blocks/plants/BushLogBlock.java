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

package com.teammoeg.caupona.blocks.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BushLogBlock extends Block {
	public static final BooleanProperty FOILAGED = BooleanProperty.create("fully_foliaged");
	public static final BooleanProperty[] CDIRS = new BooleanProperty[] { BooleanProperty.create("n"),
			BooleanProperty.create("s"), BooleanProperty.create("w"), BooleanProperty.create("e") };
	static final VoxelShape shape = Block.box(4, 0, 4, 12, 16, 12);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	public BushLogBlock(Properties p_49795_) {
		super(p_49795_);
		BlockState def = super.defaultBlockState().setValue(FOILAGED, false);
		for (BooleanProperty i : CDIRS) {
			def = def.setValue(i, false);
		}
		this.registerDefaultState(def);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FOILAGED);
		for (BooleanProperty i : CDIRS)
			builder.add(i);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FOILAGED, false);
	}

	public static BlockState setFullShape(BlockState state) {
		for (BooleanProperty i : CDIRS)
			state = state.setValue(i, true);
		return state.setValue(FOILAGED, true);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		if (pDirection.getStepY() != 0)
			return pState;
		if (pNeighborState.is(BlockTags.LEAVES)) {
			pState = pState.setValue(CDIRS[pDirection.ordinal() - 2], true);
		} else
			pState = pState.setValue(CDIRS[pDirection.ordinal() - 2], false);

		for (BooleanProperty i : CDIRS) {
			if (!pState.getValue(i))
				return pState.setValue(FOILAGED, false);
		}
		return pState.setValue(FOILAGED, true);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}

}
