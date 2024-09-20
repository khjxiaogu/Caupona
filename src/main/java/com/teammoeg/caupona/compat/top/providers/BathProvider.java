package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.blocks.hypocaust.BathHeatingBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BathProvider implements IProbeInfoProvider {

	public BathProvider() {
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof BathHeatingBlockEntity entity) {
			info.progress(entity.getHeat(), 2, info.defaultProgressStyle().showText(false).filledColor(0xffd7454f).alternateFilledColor(0xffd7454f).backgroundColor(0xff91000a));
		}
	}

	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("bath_heat");
	}

}
