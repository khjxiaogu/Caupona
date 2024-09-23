package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.compat.top.TOPRegister;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GravyBoatProvider implements IProbeInfoProvider {

	public GravyBoatProvider() {
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(state.hasProperty(GravyBoatBlock.LEVEL)) {
			int damage=state.getValue(GravyBoatBlock.LEVEL);
			info.progress(5-damage, 5, info.defaultProgressStyle().filledColor(0xffe9d892).alternateFilledColor(0xffe9d892));
		}
	}

	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("gravt_boat");
	}

}
