package com.teammoeg.caupona.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KitchenRailBlock extends Block {
	protected static final VoxelShape OCTET_N = Block.box(0.0D , 0.0D, 0.0D , 16.0D, 0.0D, 8.0D );
	protected static final VoxelShape OCTET_W = Block.box(0.0D , 0.0D, 0.0D , 8.0D , 16.0D, 16.0D);
	protected static final VoxelShape OCTET_E = Block.box(8.0D, 0.0D, 0.0D , 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape OCTET_S = Block.box(0.0D , 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	public KitchenRailBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
		case NORTH:return OCTET_N;
		case SOUTH:return OCTET_S;
		case WEST:return OCTET_W;
		case EAST:return OCTET_E;
		default:return super.getShape(state, level, pos, context);
		}
	}

}
