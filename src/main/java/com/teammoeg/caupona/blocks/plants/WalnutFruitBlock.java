package com.teammoeg.caupona.blocks.plants;

import com.teammoeg.caupona.CPConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
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
}
