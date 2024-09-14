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

package com.teammoeg.caupona.blocks.loaf;

import com.teammoeg.caupona.blocks.decoration.SelfStackingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LoafBlock extends SelfStackingBlock {
	public LoafBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	protected static final VoxelShape BOTTOM_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape TOP_AABB = Block.box(2.0, 8.0, 2.0, 14.0, 14.0, 14.0);
	protected static final VoxelShape BOTH_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
	protected static final VoxelShape BOTTOM_COLL_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	protected static final VoxelShape TOP_COLL_AABB = Block.box(5.0, 6.0, 5.0, 11.0, 16.0, 11.0);
	protected static final VoxelShape BOTH_COLL_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        SlabType slabtype = state.getValue(TYPE);
        switch (slabtype) {
            case DOUBLE:
                return BOTH_COLL_AABB;
            case TOP:
                return TOP_COLL_AABB;
            default:
                return BOTTOM_COLL_AABB;
        }
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		SlabType slabtype = state.getValue(TYPE);
		switch (slabtype) {
		case DOUBLE:
			return BOTH_AABB;
		case TOP:
			return TOP_AABB;
		default:
			return BOTTOM_AABB;
		}
	}
    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(TYPE)==SlabType.DOUBLE?14:12;
    }
}
