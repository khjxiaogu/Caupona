/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.caupona.blocks.pot;

import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.client.CPParticles;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.registries.DeferredHolder;

public class StewPot extends CPRegisteredEntityBlock<StewPotBlockEntity> implements LiquidBlockContainer {
	public static final EnumProperty<Axis> FACING = BlockStateProperties.HORIZONTAL_AXIS;

	public StewPot(Properties blockProps, DeferredHolder<BlockEntityType<?>,BlockEntityType<StewPotBlockEntity>> ste) {
		super(blockProps, ste);
	}

	static final VoxelShape shape = Block.box(1, 0, 1, 15, 12, 15);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held,BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		ItemInteractionResult p = super.useItemOn(held,state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) worldIn.getBlockEntity(pos);
		if (blockEntity.canAddFluid()) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				blockEntity.getTank().setFluid(FluidStack.EMPTY);
				return ItemInteractionResult.SUCCESS;
			}
			FluidStack out=Utils.extractFluid(held);
			if (!out.isEmpty()) {
				if (blockEntity.tryAddFluid(out)) {
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					if (!player.addItem(ret))
						player.drop(ret, false);
				}

				return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
			}
			if (FluidUtil.interactWithFluidHandler(player, handIn, blockEntity.getTank()))
				return ItemInteractionResult.SUCCESS;

		}
		return p;
	}

	@Override
	public boolean canPlaceLiquid(Player ps,BlockGetter w, BlockPos p, BlockState s, Fluid f) {
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) w.getBlockEntity(p);
		return blockEntity.canAddFluid(new FluidStack(f, 1000));
	}

	@Override
	public boolean placeLiquid(LevelAccessor w, BlockPos p, BlockState s, FluidState f) {
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) w.getBlockEntity(p);
		if (blockEntity.tryAddFluid(new FluidStack(f.getType(), 1000))) {
			return true;
		}
		return false;
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		if (worldIn.getBlockEntity(pos) instanceof StewPotBlockEntity pot) {
			if (pot.proctype == 2 && pot.working) {

				int count = 2;
				while (--count != 0)
					worldIn.addParticle(CPParticles.STEAM.get(), pos.getX() + rand.nextFloat(), pos.getY() + 1,pos.getZ() + rand.nextFloat(), 0.0D,
							0.0D, 0.0D);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (worldIn.getBlockEntity(pos) instanceof StewPotBlockEntity pot && state.getBlock() != newState.getBlock()) {
			if (pot.proctype != 2)
				for (int i = 0; i < 9; i++) {
					ItemStack is = pot.getInv().getStackInSlot(i);
					if (!is.isEmpty())
						super.popResource(worldIn, pos, is);
				}
			for (int i = 9; i < 12; i++) {
				ItemStack is = pot.getInv().getStackInSlot(i);
				if (!is.isEmpty())
					super.popResource(worldIn, pos, is);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getAxis());

	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) pLevel.getBlockEntity(pPos);
		if (blockEntity.proctype == 0) {
			int ret = 1;
			for (int i = 0; i < 9; i++) {
				ItemStack is = blockEntity.getInv().getStackInSlot(i);
				if (!is.isEmpty())
					ret++;

			}
			ret += blockEntity.getTank().getFluidAmount() / 250;
			return ret;
		}
		return 0;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		// TODO Auto-generated method stub
		InteractionResult p= super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) level.getBlockEntity(pos);
		if (blockEntity != null && !level.isClientSide&&(player.getAbilities().instabuild||!blockEntity.isInfinite)) {
			((ServerPlayer) player).openMenu( blockEntity, blockEntity.getBlockPos());
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}


}
