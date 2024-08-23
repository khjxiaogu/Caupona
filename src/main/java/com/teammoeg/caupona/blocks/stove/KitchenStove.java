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

package com.teammoeg.caupona.blocks.stove;

import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;

public class KitchenStove extends CPRegisteredEntityBlock<KitchenStoveBlockEntity> {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty ASH = BooleanProperty.create("ash");
	public static final IntegerProperty FUELED = IntegerProperty.create("fueled", 0, 3);

	public KitchenStove(Properties blockProps, DeferredHolder<BlockEntityType<?>,BlockEntityType<KitchenStoveBlockEntity>> ste) {
		super(blockProps, ste);
	}

	/*
	 * @Override
	 * public VoxelShape getVisualShape(BlockState state, BlockGetter reader,
	 * BlockPos pos, CollisionContext context) {
	 * return Shapes.empty();
	 * }
	 */
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
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos bp, RandomSource rand) {
		if (stateIn.getValue(LIT)) {
			double d0 = bp.getX();
			double d1 = bp.getY();
			double d2 = bp.getZ();
			if (rand.nextDouble() < 0.2D) {
				worldIn.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F,
						false);
			}
			worldIn.addParticle(ParticleTypes.FLAME, d0 + rand.nextDouble(), bp.getY() + 1, d2 + rand.nextDouble(),
					0.0D, 0.0D, 0.0D);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()&&worldIn.getBlockEntity(pos) instanceof KitchenStoveBlockEntity stove) {
			ItemStack is = stove.getItem(0);
			if (!is.isEmpty())
				super.popResource(worldIn, pos, is);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(LIT).add(FUELED).add(ASH);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(LIT, false).setValue(ASH, false).setValue(FUELED, 0).setValue(FACING,
				context.getHorizontalDirection().getOpposite());

	}

	static final VoxelShape shape = Shapes.or(Shapes.or(Block.box(0, 0, 0, 16, 14, 16), Block.box(0, 14, 0, 2, 16, 16)),
			Shapes.or(Block.box(0, 14, 0, 16, 16, 2),
					Shapes.or(Block.box(14, 14, 0, 16, 16, 16), Block.box(0, 14, 14, 16, 16, 16))));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		if (pState.getValue(LIT)) {
			pEntity.hurt(pLevel.damageSources().hotFloor(), 1);
		}
		super.stepOn(pLevel, pPos, pState, pEntity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		KitchenStoveBlockEntity blockEntity = (KitchenStoveBlockEntity) worldIn.getBlockEntity(pos);

			if (blockEntity != null &&(player.getAbilities().instabuild||!blockEntity.isInfinite)) {
				if(!worldIn.isClientSide) {
				((ServerPlayer) player).openMenu(blockEntity, blockEntity.getBlockPos());
				}
				return InteractionResult.sidedSuccess(worldIn.isClientSide());
			}
		return p;
	}


}
