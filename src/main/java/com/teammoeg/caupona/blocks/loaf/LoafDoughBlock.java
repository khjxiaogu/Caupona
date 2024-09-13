package com.teammoeg.caupona.blocks.loaf;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.blocks.CPEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class LoafDoughBlock extends LoafBlock implements CPEntityBlock<LoafDoughBlockEntity> {
	public LoafDoughBlock(Properties properties) {
		super(properties);
	}

	@Override
	public DeferredHolder<BlockEntityType<?>, BlockEntityType<LoafDoughBlockEntity>> getBlock() {
		return CPBlockEntityTypes.LOAF_DOUGH;
	}

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(TYPE)==SlabType.DOUBLE?4:2;
    }


}
