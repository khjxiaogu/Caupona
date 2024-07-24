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
