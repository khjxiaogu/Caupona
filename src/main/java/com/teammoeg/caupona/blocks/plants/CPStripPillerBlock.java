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

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CPStripPillerBlock extends RotatedPillarBlock {
	DeferredHolder<Block,Block> stripped;

	public CPStripPillerBlock(DeferredHolder<Block,Block> stripped, Properties p_55926_) {
		super(p_55926_);
		this.stripped = stripped;
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction,
			boolean simulate) {
		if (toolAction == ItemAbilities.AXE_STRIP && stripped != null) {
			return stripped.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS,
					state.getValue(RotatedPillarBlock.AXIS));
		}
		return super.getToolModifiedState(state, context, toolAction, simulate);
	}

}
