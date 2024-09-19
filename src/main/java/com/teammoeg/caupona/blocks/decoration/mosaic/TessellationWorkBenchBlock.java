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

package com.teammoeg.caupona.blocks.decoration.mosaic;

import javax.annotation.Nullable;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TessellationWorkBenchBlock extends CPHorizontalBlock {

	public TessellationWorkBenchBlock(Properties pProperties) {
		super(pProperties);
		CODEC = simpleCodec(TessellationWorkBenchBlock::new);
	}
	static final VoxelShape shape = Block.box(0, 0, 0, 16, 11, 16);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
	private static final Component CONTAINER_TITLE = Component
			.translatable("container." + CPMain.MODID + ".tessellation_workbench.title");

	@Nullable
	public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
		return new SimpleMenuProvider((p_57074_, p_57075_, p_57076_) -> {
			return new TBenchMenu(p_57074_, p_57075_, ContainerLevelAccess.create(pLevel, pPos));
		}, CONTAINER_TITLE);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		player.openMenu(this.getMenuProvider(state, level, pos),pos);
		return InteractionResult.CONSUME;
	}
}
