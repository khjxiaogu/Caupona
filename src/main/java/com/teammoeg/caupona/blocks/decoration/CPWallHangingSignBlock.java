package com.teammoeg.caupona.blocks.decoration;

import com.teammoeg.caupona.CPBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CPWallHangingSignBlock extends WallHangingSignBlock {

	public CPWallHangingSignBlock(WoodType type, Properties properties) {
		super(type, properties);
		CPBlocks.hanging_signs.add(this);
	}
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new CPHangingSignBlockEntity(pPos, pState);
	}
}
