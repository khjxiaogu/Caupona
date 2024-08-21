package com.teammoeg.caupona.blocks.decoration.mosaic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public class MosaicData {
	MosaicPattern pattern;
	MosaicMaterial material1;
	MosaicMaterial material2;
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
	public MosaicData() {

	}
	public MosaicData(MosaicPattern pattern, MosaicMaterial material1, MosaicMaterial material2) {
		super();
		this.pattern = pattern;
		this.material1 = material1;
		this.material2 = material2;
	}
	public MosaicPattern getPattern() {
		return pattern;
	}
	public void setPattern(MosaicPattern pattern) {
		this.pattern = pattern;
	}
	public MosaicMaterial getMaterial1() {
		return material1;
	}
	public void setMaterial1(MosaicMaterial material1) {
		this.material1 = material1;
	}
	public MosaicMaterial getMaterial2() {
		return material2;
	}
	public void setMaterial2(MosaicMaterial material2) {
		this.material2 = material2;
	}
	public BlockState createBlock() {
		return CPBlocks.MOSAIC.get().defaultBlockState().setValue(MosaicBlock.MATERIAL_1, material1).setValue(MosaicBlock.MATERIAL_2, material2).setValue(MosaicBlock.PATTERN,pattern );
	}
}
