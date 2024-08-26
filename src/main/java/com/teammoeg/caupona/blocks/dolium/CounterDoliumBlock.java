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

package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CounterDoliumBlock extends CPHorizontalEntityBlock<CounterDoliumBlockEntity> implements LiquidBlockContainer {

	public CounterDoliumBlock(Properties p) {
		super(CPBlockEntityTypes.DOLIUM, p);
		CPBlocks.dolium.add(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	static final VoxelShape shape = Shapes.or(Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 0, 4, 16, 16)),
			Shapes.or(Block.box(0, 4, 0, 16, 16, 4),
					Shapes.or(Block.box(12, 4, 0, 16, 16, 16), Block.box(0, 4, 12, 16, 16, 16))));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}
	@Override
	public ItemInteractionResult useItemOn(ItemStack held,BlockState state, Level worldIn, BlockPos pos, Player player,InteractionHand hand,
			BlockHitResult hit) {
		ItemInteractionResult p = super.useItemOn(held,state, worldIn, pos, player, hand, hit);
		if (p.consumesAction())
			return p;
		if(worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				dolium.tank.setFluid(FluidStack.EMPTY);
				return ItemInteractionResult.SUCCESS;
			}
			FluidStack out=Utils.extractFluid(held);
			if (!out.isEmpty()) {
				if (dolium.tryAddFluid(out)) {
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					if (!player.addItem(ret))
						player.drop(ret, false);
				}
				return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
			}
			if (FluidUtil.interactWithFluidHandler(player, hand, dolium.tank))
				return ItemInteractionResult.SUCCESS;
		}
		return p;
	}
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,
			BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		if(worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
				if (!worldIn.isClientSide&&(player.getAbilities().instabuild||!dolium.isInfinite))
					((ServerPlayer) player).openMenu(dolium, dolium.getBlockPos());
				
		}
		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public boolean canPlaceLiquid(Player ps,BlockGetter w, BlockPos p, BlockState s, Fluid f) {
		if(w.getBlockEntity(p) instanceof CounterDoliumBlockEntity dolium)
			return dolium.tank.fill(new FluidStack(f, 1000), FluidAction.SIMULATE) == 1000;
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor w, BlockPos p, BlockState s, FluidState f) {
		if(w.getBlockEntity(p) instanceof CounterDoliumBlockEntity dolium)
		if (dolium.tryAddFluid(new FluidStack(f.getType(), 1000)))
			return true;
		return false;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock() && worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
			for (int i = 0; i < 6; i++) {
				ItemStack is = dolium.inv.getStackInSlot(i);
				if (!is.isEmpty())
					super.popResource(worldIn, pos, is);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

}
