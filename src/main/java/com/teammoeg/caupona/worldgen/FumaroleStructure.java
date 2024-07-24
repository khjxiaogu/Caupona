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

package com.teammoeg.caupona.worldgen;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPWorldGen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public class FumaroleStructure extends Structure {
	  public static final MapCodec<FumaroleStructure> CODEC = RecordCodecBuilder.<FumaroleStructure>mapCodec((p_227640_) -> {
	      return p_227640_.group(settingsCodec(p_227640_), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((p_227656_) -> {
	         return p_227656_.startPool;
	      })).apply(p_227640_, FumaroleStructure::new);
	   });
	public final Holder<StructureTemplatePool> startPool;

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}


	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
		BlockPos blockpos = ctx.chunkPos().getBlockAt(ctx.random().nextInt(16), 0, ctx.random().nextInt(16));
		int topLandY = ctx.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(),
				Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());
		blockpos = blockpos.atY(topLandY - 4 + ctx.random().nextInt(1));
		return JigsawPlacement.addPieces(ctx, this.startPool,Optional.empty(), 32, blockpos,false, Optional.empty(), 0, PoolAliasLookup.EMPTY,JigsawStructure.DEFAULT_DIMENSION_PADDING,
            JigsawStructure.DEFAULT_LIQUID_SETTINGS);
	}

	@Override
	public StructureType<?> type() {
		return CPWorldGen.FUMAROLE.get();
	}


	public FumaroleStructure(StructureSettings p_226558_,Holder<StructureTemplatePool> pool) {
		super(p_226558_);
		startPool=pool;
	}


}