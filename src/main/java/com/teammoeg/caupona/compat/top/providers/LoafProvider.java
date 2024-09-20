package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.blocks.loaf.LoafDoughBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LoafProvider implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof LoafDoughBlockEntity entity) {
			info.progress(entity.process, CPConfig.COMMON.loafCooking.get(),info.defaultProgressStyle().showText(false));
		}
	}
	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("loaf_dough");
	}

}
