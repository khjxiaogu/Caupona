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

package com.teammoeg.caupona.blocks.pan;

import java.util.List;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class GravyBoatBlock extends CPHorizontalBlock implements ICreativeModeTabItem{
	public static final IntegerProperty LEVEL = IntegerProperty.create("damage", 0, 5);

	public GravyBoatBlock(Properties p_54120_) {
		super(p_54120_);
		CODEC = simpleCodec(GravyBoatBlock::new);
	}

	static final VoxelShape shapeNS = Block.box(3, 0, 4, 13, 7, 12);
	static final VoxelShape shapeEW = Block.box(4, 0, 3, 12, 7, 13);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(FACING).getAxis() == Axis.Z)
			return shapeNS;
		return shapeEW;

	}

	public static int getOil(BlockState pState) {
		return 5 - pState.getValue(LEVEL);
	}

	public static boolean drawOil(Level pLevel, BlockPos pPos, int count) {
		BlockState pState=pLevel.getBlockState(pPos);
		int dmg = pState.getValue(LEVEL);
		if (dmg + count <= 5) {
			pState = pState.setValue(LEVEL, dmg + count);
			pLevel.setBlockAndUpdate(pPos, pState);
			return true;
		}
		return false;
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

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(LEVEL,
				context.getItemInHand().getDamageValue());

	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LEVEL);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isFoodTab()) {
			helper.accept(this);
			ItemStack is = new ItemStack(this);
			is.setDamageValue(is.getMaxDamage());
			helper.accept(is);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState,
			net.minecraft.world.level.storage.loot.LootParams.Builder pBuilder) {
		List<ItemStack> sep = super.getDrops(pState, pBuilder);
		for (ItemStack is : sep)
			if (is.is(CPItems.gravy_boat.get()))
				is.setDamageValue(pState.getValue(LEVEL));
		return sep;

	}

	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		ItemStack is = new ItemStack(CPItems.gravy_boat.get());
		is.setDamageValue(state.getValue(LEVEL));
		return is;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return 15 - (pState.getValue(LEVEL) * 3);
	}


}
