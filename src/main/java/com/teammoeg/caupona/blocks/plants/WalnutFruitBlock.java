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

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.CommonHooks;

public class WalnutFruitBlock extends FruitBlock {
	public WalnutFruitBlock(Properties p_52247_) {
		super(p_52247_);
	}

	/**
	 * Performs a random tick on a block.
	 */
	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if (pLevel.getRawBrightness(pPos, 0) >= 9) {
			int i = this.getAge(pState);
			if (i < this.getMaxAge()) {
				if (CommonHooks.canCropGrow(pLevel, pPos, pState,
						pRandom.nextInt(17) == 0)) {
					if (i == this.getMaxAge() - 1 && pLevel.dimensionTypeRegistration().getKey().equals(BuiltinDimensionTypes.NETHER)
							&& pRandom.nextDouble()<CPConfig.SERVER.leadenGenRate.get()) {
						pLevel.setBlock(pPos, this.getStateForAge(5), 2);
					} else {
						pLevel.setBlock(pPos, this.getStateForAge(i + 1), 2);
					}
					CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
				}
			}
		}

	}
	@Override
	public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
		int h = this.getAge(pState);
		int i = h + this.getBonemealAgeIncrease(pLevel);
		int j = this.getMaxAge();
		if (h <= j && i > j) {
			i = j;
		}

		pLevel.setBlock(pPos, this.getStateForAge(i), 2);
	}
	@Override
	public boolean isPathfindable(BlockState pState, PathComputationType pType) {
	      return false;
	}

	@Override
	public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		return PathType.COCOA;
	}
}
