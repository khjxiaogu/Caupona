package com.teammoeg.caupona.blocks.loaf;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(state.getValue(TYPE)!=SlabType.DOUBLE&&stack.is(this.asItem())) {
			if(!player.getAbilities().instabuild)
				stack.shrink(1);
			level.setBlock(pos, state.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	@Override
	protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		return blockState.getValue(TYPE) == SlabType.DOUBLE ? 15 : 13;
	}
}
