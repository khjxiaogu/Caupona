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

package com.teammoeg.caupona.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicMaterial;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicPattern;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public record MosaicData(MosaicPattern pattern,MosaicMaterial material1,MosaicMaterial material2){

	public static final Codec<MosaicData> CODEC=RecordCodecBuilder.create(t->t.group(
		StringRepresentable.fromValues(MosaicPattern::values).fieldOf("pattern").forGetter(o->o.pattern),
		StringRepresentable.fromValues(MosaicMaterial::values).fieldOf("mat1").forGetter(o->o.material1),
		StringRepresentable.fromValues(MosaicMaterial::values).fieldOf("mat2").forGetter(o->o.material2)
		).apply(t, MosaicData::new));

	public static final StreamCodec<ByteBuf,MosaicData> STREAM_CODEC=StreamCodec.composite(
		SerializeUtil.createEnumStreamCodec(MosaicPattern.values()),n->n.pattern,
		SerializeUtil.createEnumStreamCodec(MosaicMaterial.values()),n->n.material1,
		SerializeUtil.createEnumStreamCodec(MosaicMaterial.values()),n->n.material2,
		MosaicData::new);
	public MosaicPattern getPattern() {
		return pattern;
	}
	public MosaicMaterial getMaterial1() {
		return material1;
	}
	public MosaicMaterial getMaterial2() {
		return material2;
	}
	public BlockState createBlock() {
		return CPBlocks.MOSAIC.get().defaultBlockState().setValue(MosaicBlock.MATERIAL_1, material1).setValue(MosaicBlock.MATERIAL_2, material2).setValue(MosaicBlock.PATTERN,pattern );
	}
}
