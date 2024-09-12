package com.teammoeg.caupona.blocks.decoration;

import com.teammoeg.caupona.CPBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CPHangingSignBlockEntity extends SignBlockEntity {
    private static final int MAX_TEXT_LINE_WIDTH = 60;
    private static final int TEXT_LINE_HEIGHT = 9;
    public CPHangingSignBlockEntity(BlockPos p_250603_, BlockState p_251674_) {
        super(CPBlockEntityTypes.HANGING_SIGN.get(), p_250603_, p_251674_);
    }

    @Override
    public int getTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    @Override
    public int getMaxTextLineWidth() {
        return MAX_TEXT_LINE_WIDTH;
    }

    @Override
    public SoundEvent getSignInteractionFailedSoundEvent() {
        return SoundEvents.WAXED_HANGING_SIGN_INTERACT_FAIL;
    }
}
