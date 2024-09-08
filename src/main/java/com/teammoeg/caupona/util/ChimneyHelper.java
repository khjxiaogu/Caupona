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

package com.teammoeg.caupona.util;

import com.teammoeg.caupona.CPTags.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChimneyHelper {
	private ChimneyHelper() {
	}

	public static BlockPos getNearestChimney(Level l, BlockPos from, int maxdist) {
		ChunkAccess c = l.getChunk(from);
		BlockPos start = from.above();
		BlockState bs = c.getBlockState(start);
		for (int i = 0; i < maxdist; i++) {
			if (bs.isAir() || bs.is(Blocks.CHIMNEY_IGNORES)) {
				start = start.above();
				bs = c.getBlockState(start);
			} else
				break;
		}
		if (!bs.is(Blocks.CHINMEY_BLOCK))
			return null;
		while (bs.is(Blocks.CHINMEY_BLOCK)) {
			start = start.above();
			bs = c.getBlockState(start);
		}
		if (bs.is(Blocks.CHIMNEY_POT) || bs.isAir() || bs.is(Blocks.CHIMNEY_IGNORES))
			return start;
		return null;
	}
}
