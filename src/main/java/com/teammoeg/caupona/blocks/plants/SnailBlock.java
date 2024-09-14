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

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.common.CommonHooks;

public class SnailBlock extends FruitBlock {
	public static final BooleanProperty EATEN_FRUIT = BooleanProperty.create("plump");
	
	
	public SnailBlock(Properties p_52247_) {
		super(p_52247_);
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0))
				.setValue(EATEN_FRUIT, false));
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return pLevel.getBlockState(pPos.above()).is(CPTags.Blocks.SNAIL_GROWABLE_ON);
	}

	public boolean isRandomlyTicking(BlockState pState) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		return false;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}

	public int getMaxAge(BlockState state) {
		return state.getValue(EATEN_FRUIT) ? 4 : 3;
	}

	public BlockState getStateForAge(BlockState orig, int pAge) {
		return orig.setValue(this.getAgeProperty(), Integer.valueOf(pAge));
	}

	/**
	 * Performs a random tick on a block.
	 */
	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		int i = this.getAge(pState);
		if (i < this.getMaxAge(pState)) {
			
			if (CommonHooks.canCropGrow(pLevel, pPos, pState,pRandom.nextInt(3) == 0)) {
				int eat=eatFirstEdible(pPos,pLevel);
				if(eat>0) {
					if(eat==2)
						pState=pState.setValue(EATEN_FRUIT, true);
					pLevel.setBlock(pPos, this.getStateForAge(pState, i + 1), 2);
					CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
				}
			}
		}else {
			if(pRandom.nextInt(3) == 0) {
				BlockPos growable=findGrowableBlock(pPos,pLevel);
				if(growable!=null) {
					BlockState state=pLevel.getBlockState(growable);
					int eat=eatFirstEdible(pPos,pLevel);
					if(eat>0) {
						if(state.is(CPTags.Blocks.SNAIL_FOOD)||eat==2) {
							trySpread(growable,pLevel);
						}else if(pRandom.nextInt(2)==0){
							trySpread(growable,pLevel);
						}
					}
				}
			}
		}
		
	}
	private static final BlockPos[] NEIGH=new BlockPos[] {
			new BlockPos(1,0,0),
			new BlockPos(-1,0,0),
			new BlockPos(0,0,1),
			new BlockPos(0,0,-1)
	};
	public BlockPos findGrowableBlock(BlockPos pos,ServerLevel level) {
		for(BlockPos bp:NEIGH) {
			BlockPos cur=pos.offset(bp);
			if(this.canSurvive(this.defaultBlockState(), level, cur)) {
				BlockState down=level.getBlockState(cur);
				if(down.is(Blocks.AIR)||down.canBeReplaced()||down.is(CPTags.Blocks.SNAIL_FOOD))
					return cur;
			}
		}
		return null;
	}
	public void trySpread(BlockPos pos,ServerLevel level) {
		level.setBlock(pos,Blocks.AIR.defaultBlockState(), 0);
		if(level.getBlockState(pos.above()).is(Blocks.AIR))
			level.setBlock(pos.above(), CPBlocks.SNAIL_MUCUS.get().defaultBlockState(), 2);
		level.setBlock(pos,this.defaultBlockState(), 2);
	}
	private static final int[] ORDER=new int[] {0,-1,1};
	public int eatFirstEdible(BlockPos pos,ServerLevel level) {
		int res=eatBlock(pos.above(),level,true);
		if(res>0)return res;
		for(int x:ORDER) {
			for(int z:ORDER) {
				res=eatBlock(pos.offset(x, 0, z),level,false);
				if(res>0)
					return res;
			}
		}
		for(int y=2;y>0;y--) {
			for(int x:ORDER) {
				for(int z:ORDER) {
					res=eatBlock(pos.offset(x, y, z),level,y==1&&x==0&&z==0);
					if(res>0)
						return res;
				}
			}
		}
		return 0;
	}

	public int eatBlock(BlockPos pos,ServerLevel level,boolean toMucus) {
		BlockState bs=level.getBlockState(pos);
		if(bs.is(CPTags.Blocks.SNAIL_FOOD)) {
			
			Block blk=bs.getBlock();
			boolean isplump=bs.is(CPTags.Blocks.SNAIL_PLUMP_FOOD);
			if(blk instanceof CropBlock cb) {
				if(cb.isMaxAge(bs)) {
					level.setBlock(pos,Blocks.AIR.defaultBlockState(), 2);
					return isplump?2:1;
				}
			}else {
				if(toMucus||level.getBlockState(pos.below()).is(CPBlocks.SNAIL.get())) {
					level.setBlock(pos, CPBlocks.SNAIL_MUCUS.get().defaultBlockState(), 2);
					return isplump?2:1;
				}
				level.setBlock(pos,Blocks.AIR.defaultBlockState(), 2);
				return isplump?2:1;
				
			}
		}
		return 0;
	}
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(AGE, EATEN_FRUIT);
	}

}
