package com.teammoeg.caupona.blocks;

import java.util.function.BiFunction;

import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;

public class BowlBlock extends CPBaseBlock<BowlTileEntity>{

	public BowlBlock(String name, Properties blockProps, RegistryObject<BlockEntityType<BowlTileEntity>> ste,
			BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(name, blockProps, ste, createItemBlock);
	}
	static final VoxelShape shape = Block.box(2.8, 0, 2.8, 13.2, 5.2, 13.2);

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
			CollisionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof BowlTileEntity && state.getBlock() != newState.getBlock()) {
			BowlTileEntity te = (BowlTileEntity) tileEntity;
			super.popResource(worldIn, pos, te.internal);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
			InteractionHand handIn, BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		BowlTileEntity tileEntity = (BowlTileEntity) worldIn.getBlockEntity(pos);
		if(tileEntity.internal!=null&&tileEntity.internal.getItem() instanceof StewItem) {
			CauponaApi.applyStew(worldIn, player,CauponaApi.getInfo(tileEntity.internal));
			tileEntity.internal=tileEntity.internal.getContainerItem();
			tileEntity.syncData();
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
		if (tileEntity instanceof BowlTileEntity) {
			BowlTileEntity te = (BowlTileEntity) tileEntity;
			te.internal=ItemHandlerHelper.copyStackWithSize(pStack, 1);
		}
	}


}