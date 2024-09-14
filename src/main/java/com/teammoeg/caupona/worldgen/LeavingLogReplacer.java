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

package com.teammoeg.caupona.worldgen;

import java.util.HashSet;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPWorldGen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class LeavingLogReplacer extends TreeDecorator {
	public static final MapCodec<LeavingLogReplacer> CODEC = RecordCodecBuilder.mapCodec(t ->
	t.group(BlockStateProvider.CODEC.fieldOf("replace").forGetter(o->o.state)).apply(t, LeavingLogReplacer::new)
		);
	BlockStateProvider state;
	public LeavingLogReplacer(BlockStateProvider state) {
		super();
		this.state = state;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return CPWorldGen.BUSH_PLACER.get();
	}

	@Override
	public void place(Context pContext) {
		Set<BlockPos> leaves=new HashSet<>(pContext.leaves());
		for(BlockPos bp:pContext.logs()){
			boolean n=leaves.contains(bp.north());
			boolean s=leaves.contains(bp.south());
			boolean e=leaves.contains(bp.east());
			boolean w=leaves.contains(bp.west());
			//System.out.println("n:"+n+"s:"+s+"e:"+e+"w:"+w);
			if(n||s||w||e) {
				pContext.setBlock(bp, state.getState(pContext.random(), bp));
			}
		}
	}

}
