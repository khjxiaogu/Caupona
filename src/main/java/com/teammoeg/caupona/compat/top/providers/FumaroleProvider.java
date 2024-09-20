package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlock;
import com.teammoeg.caupona.compat.top.TOPRegister;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FumaroleProvider implements IProbeInfoProvider {

	public FumaroleProvider() {
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(state.hasProperty(FumaroleVentBlock.HEAT)) {
			int heat=state.getValue(FumaroleVentBlock.HEAT);
			info.progress(heat, 2, info.defaultProgressStyle().showText(false).filledColor(0xffd7454f).alternateFilledColor(0xffd7454f).backgroundColor(0xff91000a));
		}
	}

	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("fumarole_heat");
	}

}
