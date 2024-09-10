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

package com.teammoeg.caupona.blocks.plants;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FruitsLeavesBlock extends LeavesBlock implements BonemealableBlock {
	DeferredHolder<Block,Block> fruit;

	public FruitsLeavesBlock(Properties p_54422_, DeferredHolder<Block,Block> fruit2) {
		super(p_54422_);
		this.fruit = fruit2;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!pState.getValue(PERSISTENT)) {
			if (pState.getValue(DISTANCE) == 7) {
				dropResources(pState, pLevel, pPos);
				pLevel.removeBlock(pPos, false);
			} else {
				BlockPos below = pPos.below();
				if (pRandom.nextInt(51) == 0 && pLevel.getRawBrightness(below, 0) >= 9) {
					if (pLevel.getBlockState(below).isAir()&&shouldPlaceFruit(pLevel,below)) {
						pLevel.setBlockAndUpdate(below, fruit.get().defaultBlockState());
					}
				}
			}
		}
	}
	public boolean shouldPlaceFruit(Level pLevel, BlockPos pPos) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return false;
		int cnt = 0;
		
		AABB aabb = AABB.ofSize(pPos.getCenter(),2,0,2);
		Iterator<BlockState> it = pLevel.getBlockStates(aabb).iterator();
		while (it.hasNext()) {
			if (it.next().getBlock() == fruit.get())
				cnt++;
			if (cnt >= 2)
				return false;
		}
		return true;
	}
	public boolean isRandomlyTicking(BlockState pState) {
		return !pState.getValue(PERSISTENT);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return !pState.getValue(PERSISTENT) && pState.getValue(DISTANCE) != 7;
	}

	@Override
	public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		return pRandom.nextInt(4) == 0;
	}

	@Override
	public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		if (pLevel.getBlockState(pPos.below()).isAir()) {
			pLevel.setBlockAndUpdate(pPos.below(), fruit.get().defaultBlockState());
		}
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 60;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 30;
	}

}
