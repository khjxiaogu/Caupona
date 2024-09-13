package com.teammoeg.caupona.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class SelfStackingBlock extends SlabBlock {

	public SelfStackingBlock(Properties properties) {
		super(properties);
	}
	public ReplacableType checkPlacable(BlockState s) {
		if(s.isEmpty())
			return ReplacableType.EMPTY;
		if(s.getBlock().asItem()==this.asItem()) {
			if(s.getValue(TYPE)==SlabType.DOUBLE)
				return ReplacableType.FULL;
			return ReplacableType.ALLOW;
		}
		return ReplacableType.DENY;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(state.getValue(TYPE)!=SlabType.DOUBLE){
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this.asItem()));
			level.removeBlock(pos, false);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		for(int i=1;;i++) {
			BlockPos newPos=pos.below(i);
			BlockState bs=level.getBlockState(newPos);
			ReplacableType type=checkPlacable(bs);
			if(type.shouldSkip())
				break;
			if(type.isAllowed()) {
				if(type==ReplacableType.ALLOW)
					level.removeBlock(newPos, false);
				else
					level.setBlock(newPos.above(), this.defaultBlockState().setValue(TYPE, SlabType.TOP), UPDATE_ALL);
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this.asItem()));
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		for(int i=1;;i++) {
			BlockPos newPos=pos.above(i);
			BlockState bs=level.getBlockState(newPos);
			ReplacableType type=checkPlacable(bs);
			if(type.isAllowed()||type==ReplacableType.DENY) {
				if(type==ReplacableType.ALLOW)
					level.removeBlock(newPos, false);
				else
					level.setBlock(newPos.below(), this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM), UPDATE_ALL);
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this.asItem()));
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		//return super.useWithoutItem(state, level, pos, player, hitResult);
	}
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(stack.is(this.asItem())) {
			if(state.getValue(TYPE)!=SlabType.DOUBLE){
				if(!player.getAbilities().instabuild)
					stack.shrink(1);
				level.setBlock(pos, state.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			}
			for(int i=1;;i++) {
				BlockPos newPos=pos.above(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM), UPDATE_ALL);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			for(int i=1;;i++) {
				BlockPos newPos=pos.below(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, this.defaultBlockState().setValue(TYPE, SlabType.TOP), UPDATE_ALL);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return (level.getBlockState(pos.below()).isSolid()&&state.getValue(TYPE)!=SlabType.TOP)||(level.getBlockState(pos.above()).isSolid()&&state.getValue(TYPE)!=SlabType.BOTTOM);
	}
    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing.getAxis()!=Axis.Y||canSurvive(state,level,currentPos)
            ? super.updateShape(state, facing, facingState, level, currentPos, facingPos)
            : Blocks.AIR.defaultBlockState();
    }
}
