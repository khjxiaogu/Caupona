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

package com.teammoeg.caupona.blocks.foods;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.item.DishItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DishBlock extends CPRegisteredEntityBlock<DishBlockEntity> {

	public DishBlock(Properties blockProps) {
		super(blockProps, CPBlockEntityTypes.DISH);
		CPBlocks.dishes.add(this);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}


	static final VoxelShape shape = Block.box(0, 0, 0, 16, 3, 16);

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof DishBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof DishBlockEntity dish) {

				super.popResource(worldIn, pos, dish.internal);
			}
			worldIn.removeBlockEntity(pos);
		}

	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,
			BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof DishBlockEntity dish &&dish.internal != null && dish.internal.getItem() instanceof DishItem
				&& dish.internal.getFoodProperties(null)!=null) {
			FoodProperties fp = dish.internal.getFoodProperties(player);
			if (dish.isInfinite) {
				if (player.canEat(fp.canAlwaysEat())) {
					player.eat(worldIn, dish.internal.copy());
					dish.syncData();
				}
			} else {
				if (player.canEat(fp.canAlwaysEat())) {
					ItemStack iout = player.eat(worldIn, dish.internal);
					dish.internal = iout;
					if (dish.internal.is(Items.BOWL)) {
						worldIn.setBlockAndUpdate(pos, CPBlocks.DISH.get().defaultBlockState());
					} else {
						worldIn.removeBlock(pos, false);
					}
					dish.syncData();
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof DishBlockEntity dish) {
			dish.setComponents(DataComponentMap.EMPTY);
			dish.internal = pStack.copyWithCount(1);
		}
	}

	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		if (level.getBlockEntity(pos) instanceof DishBlockEntity dish) {
			if (dish.internal == null)
				return ItemStack.EMPTY;
			return dish.internal.copy();
		}
		return this.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		if (pLevel.getBlockEntity(pPos) instanceof DishBlockEntity dish)
			if (dish.internal != null && !dish.internal.isEmpty() && dish.internal.getFoodProperties(null)!=null) 
				return 15;
		
		return 0;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}
}
