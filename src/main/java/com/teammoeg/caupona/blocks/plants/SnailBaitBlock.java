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

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTags.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

public class SnailBaitBlock extends FruitBlock {

	public SnailBaitBlock(Properties p_52247_) {
		super(p_52247_);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return pLevel.getBlockState(pPos.above()).is(Blocks.SNAIL_GROWABLE_ON);
	}

	@Override
	public int getMaxAge() {
		return 7;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light

		int i = this.getAge(pState);
		if (i < this.getMaxAge()) {
			if (CommonHooks.canCropGrow(pLevel, pPos, pState, pRandom.nextInt(17) == 0)) {
				pLevel.setBlock(pPos, this.getStateForAge(i + 1), 2);
				CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
			}
		} else {
			pLevel.setBlock(pPos, CPBlocks.SNAIL.get().getStateForAge(1), 2);
		}

	}

	public boolean isRandomlyTicking(BlockState pState) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return false;
	}
}
