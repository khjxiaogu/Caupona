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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SilphiumBlock extends DoublePlantBlock implements BonemealableBlock {
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public SilphiumBlock(Properties pProperties) {
		super(pProperties);
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE;
	}

	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return true;
	}

	public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		return pRandom.nextFloat() < 0.1;
	}

	public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		int dy=pState.getValue(HALF)==DoubleBlockHalf.UPPER?0:1;
		for (int i = 0; i < 3; i++) {
			int dx = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(3));
			int dz = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(3));
			if (dx == 0 && dz == 0) {
				i++;
				continue;
			}
			for(int j=0;j<3;j++) {
				BlockPos pendPos = pPos.offset(dx, dy-j, dz);
				BlockState b0 = pLevel.getBlockState(pendPos);
				BlockState b1 = pLevel.getBlockState(pendPos.below());
				BlockState b2 = pLevel.getBlockState(pendPos.above());
				if (b0.isAir()&&b2.isAir()) {
					if (b1.is(Blocks.GRASS_BLOCK)) {
						pLevel.setBlockAndUpdate(pendPos,this.defaultBlockState());
						pLevel.setBlockAndUpdate(pendPos.above(),this.defaultBlockState().setValue(HALF,DoubleBlockHalf.UPPER));
						return;
					}
				}
			}
		}
	}
}
