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

package com.teammoeg.caupona.blocks.decoration;

import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CPRoadSideBlock extends CPRoadBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	
	protected static final VoxelShape OCTET_NW = Block.box(0.0D , 14.0D, 0.0D , 6.0D , 24.0D, 6.0D );
	protected static final VoxelShape OCTET_SW = Block.box(0.0D , 14.0D, 10.0D, 6.0D , 24.0D, 16.0D);
	protected static final VoxelShape OCTET_NE = Block.box(10.0D, 14.0D, 0.0D , 16.0D, 24.0D, 6.0D );
	protected static final VoxelShape OCTET_SE = Block.box(10.0D, 14.0D, 10.0D, 16.0D, 24.0D, 16.0D);

	protected static final VoxelShape OCTET_N = Block.box(0.0D , 14.0D, 0.0D , 16.0D, 24.0D, 6.0D );
	protected static final VoxelShape OCTET_W = Block.box(0.0D , 14.0D, 0.0D , 6.0D , 24.0D, 16.0D);
	protected static final VoxelShape OCTET_E = Block.box(10.0D, 14.0D, 0.0D , 16.0D, 24.0D, 16.0D);
	protected static final VoxelShape OCTET_S = Block.box(0.0D , 14.0D, 10.0D, 16.0D, 24.0D, 16.0D);
	protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes();

	private static VoxelShape[] makeShapes() {
		return IntStream.range(0, 16).mapToObj((p_56945_) -> {
			return makeStairShape(p_56945_);
		}).toArray((p_56949_) -> {
			return new VoxelShape[p_56949_];
		});
	}

	private static VoxelShape makeStairShape(int pBitfield) {
		VoxelShape voxelshape = BASE_AABB;

		if ((pBitfield & 1) != 0) {
			if ((pBitfield & 2) != 0) {
				voxelshape = Shapes.or(voxelshape, OCTET_N);
			}
			if ((pBitfield & 4) != 0) {
				voxelshape = Shapes.or(voxelshape, OCTET_W);
			}
			voxelshape = Shapes.or(voxelshape, OCTET_NW);
		}
		if ((pBitfield & 8) != 0) {
			if ((pBitfield & 2) != 0) {
				voxelshape = Shapes.or(voxelshape, OCTET_E);
			}
			if ((pBitfield & 4) != 0) {
				voxelshape = Shapes.or(voxelshape, OCTET_S);
			}
			voxelshape = Shapes.or(voxelshape, OCTET_SE);
		}

		if ((pBitfield & 2) != 0) {
			voxelshape = Shapes.or(voxelshape, OCTET_NE);
		}
		if ((pBitfield & 4) != 0) {
			voxelshape = Shapes.or(voxelshape, OCTET_SW);
		}

		return voxelshape;
	}

	public CPRoadSideBlock(Properties pProperties) {
		super(pProperties);
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return BASE_AABB;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		return BOTTOM_SHAPES[SHAPE_BY_STATE[this.getShapeIndex(pState)]];
	}

	private static final int[] SHAPE_BY_STATE = new int[] { 12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4,
			1, 2, 8 };

	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}

	private int getShapeIndex(BlockState pState) {
		return pState.getValue(SHAPE).ordinal() * 4 + pState.getValue(FACING).get2DDataValue();
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockPos blockpos = pContext.getClickedPos();
		BlockState blockstate = this.defaultBlockState().setValue(FACING, pContext.getPlayer().isShiftKeyDown()?pContext.getHorizontalDirection():pContext.getHorizontalDirection().getOpposite());
		return blockstate.setValue(SHAPE, getStairsShape(blockstate, pContext.getLevel(), blockpos));
	}

	/**
	 * Update the provided state given the provided neighbor direction and neighbor
	 * state, returning a new state.
	 * For example, fences make their connections to the passed in state if
	 * possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific direction
	 * passed in.
	 */
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
			BlockPos pCurrentPos, BlockPos pFacingPos) {

		return pFacing.getAxis().isHorizontal() ? pState.setValue(SHAPE, getStairsShape(pState, pLevel, pCurrentPos))
				: pState;
	}

	public static boolean isRoadBlock(BlockState pState) {
		return pState.getBlock() instanceof CPRoadSideBlock;
	}

	/**
	 * Returns a stair shape property based on the surrounding stairs from the given
	 * blockstate and position
	 */
	private static StairsShape getStairsShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		Direction direction = pState.getValue(FACING);
		BlockState blockstate = pLevel.getBlockState(pPos.relative(direction));
		if (isRoadBlock(blockstate)) {
			Direction direction1 = blockstate.getValue(FACING);
			if (direction1.getAxis() != pState.getValue(FACING).getAxis()
					&& canTakeShape(pState, pLevel, pPos, direction1.getOpposite())) {
				if (direction1 == direction.getCounterClockWise()) {
					return StairsShape.OUTER_LEFT;
				}

				return StairsShape.OUTER_RIGHT;
			}
		}

		BlockState blockstate1 = pLevel.getBlockState(pPos.relative(direction.getOpposite()));
		if (isRoadBlock(blockstate1)) {
			Direction direction2 = blockstate1.getValue(FACING);
			if (direction2.getAxis() != pState.getValue(FACING).getAxis()
					&& canTakeShape(pState, pLevel, pPos, direction2)) {
				if (direction2 == direction.getCounterClockWise()) {
					return StairsShape.INNER_LEFT;
				}

				return StairsShape.INNER_RIGHT;
			}
		}

		return StairsShape.STRAIGHT;
	}

	private static boolean canTakeShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pFace) {
		BlockState blockstate = pLevel.getBlockState(pPos.relative(pFace));
		return !isRoadBlock(blockstate) || blockstate.getValue(FACING) != pState.getValue(FACING);
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If
	 * inapplicable, returns the passed
	 * blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#rotate}
	 *             whenever
	 *             possible. Implementing/overriding is fine.
	 */
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed
	 * blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#mirror}
	 *             whenever
	 *             possible. Implementing/overriding is fine.
	 */
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		Direction direction = pState.getValue(FACING);
		StairsShape stairsshape = pState.getValue(SHAPE);
		switch (pMirror) {
		case LEFT_RIGHT:
			if (direction.getAxis() == Direction.Axis.Z) {
				switch (stairsshape) {
				case INNER_LEFT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
				case INNER_RIGHT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
				case OUTER_LEFT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
				case OUTER_RIGHT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
				default:
					return pState.rotate(Rotation.CLOCKWISE_180);
				}
			}
			break;
		case FRONT_BACK:
			if (direction.getAxis() == Direction.Axis.X) {
				switch (stairsshape) {
				case INNER_LEFT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
				case INNER_RIGHT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
				case OUTER_LEFT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
				case OUTER_RIGHT:
					return pState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
				case STRAIGHT:
					return pState.rotate(Rotation.CLOCKWISE_180);
				}
			}
		default:
			break;
		}

		return super.mirror(pState, pMirror);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING, SHAPE);
	}

	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}



}
