package com.teammoeg.caupona.blocks.loaf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LoafBlock extends SlabBlock {
	public LoafBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	protected static final VoxelShape BOTTOM_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape TOP_AABB = Block.box(2.0, 8.0, 2.0, 14.0, 14.0, 14.0);
	protected static final VoxelShape BOTH_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
	protected static final VoxelShape BOTTOM_COLL_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	protected static final VoxelShape TOP_COLL_AABB = Block.box(5.0, 6.0, 5.0, 11.0, 16.0, 11.0);
	protected static final VoxelShape BOTH_COLL_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        SlabType slabtype = state.getValue(TYPE);
        switch (slabtype) {
            case DOUBLE:
                return BOTH_COLL_AABB;
            case TOP:
                return TOP_COLL_AABB;
            default:
                return BOTTOM_COLL_AABB;
        }
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		SlabType slabtype = state.getValue(TYPE);
		switch (slabtype) {
		case DOUBLE:
			return BOTH_AABB;
		case TOP:
			return TOP_AABB;
		default:
			return BOTTOM_AABB;
		}
	}
	public ReplacableType checkPlacable(BlockState s) {
		if(s.isEmpty())
			return ReplacableType.EMPTY;
		if(s.getBlock().asItem()==this.asItem()) {
			if(s.getValue(TYPE)!=SlabType.DOUBLE)
				return ReplacableType.FULL;
			return ReplacableType.ALLOW;
		}
		return ReplacableType.DENY;
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
			for(int i=1;i<6;i++) {
				BlockPos newPos=pos.above(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.BOTTOM), UPDATE_ALL);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			for(int i=1;i<6;i++) {
				BlockPos newPos=pos.below(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.TOP), UPDATE_ALL);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isSolid()||level.getBlockState(pos.above()).isSolid();
	}
	@Override
	protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		return blockState.getValue(TYPE) == SlabType.DOUBLE ? 15 : 13;
	}
    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing.getAxis()!=Axis.Y||level.getBlockState(currentPos.below()).isSolid()||level.getBlockState(currentPos.above()).isSolid()
            ? super.updateShape(state, facing, facingState, level, currentPos, facingPos)
            : Blocks.AIR.defaultBlockState();
    }
}
