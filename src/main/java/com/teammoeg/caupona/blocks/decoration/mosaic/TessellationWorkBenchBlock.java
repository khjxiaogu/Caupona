package com.teammoeg.caupona.blocks.decoration.mosaic;

import javax.annotation.Nullable;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
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

	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		pPlayer.openMenu(this.getMenuProvider(pState, pLevel, pPos),pPos);
		return InteractionResult.CONSUME;
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
		return new SimpleMenuProvider((p_57074_, p_57075_, p_57076_) -> {
			return new TBenchMenu(p_57074_, p_57075_, ContainerLevelAccess.create(pLevel, pPos));
		}, CONTAINER_TITLE);
	}
}
