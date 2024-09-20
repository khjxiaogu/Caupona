package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlock;
import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class WolfProvider implements IProbeInfoProvider {

	public WolfProvider() {
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof WolfStatueBlockEntity entity) {
			info.progress(state.getValue(WolfStatueBlock.HEAT), 2, info.defaultProgressStyle().showText(false).filledColor(0xffd7454f).alternateFilledColor(0xffd7454f).backgroundColor(0xff91000a));
			if(entity.isVeryHot())
				info.tankSimple(1000, new FluidStack(Fluids.LAVA,1000), info.defaultProgressStyle().showText(false).height(16).width(16));
		}
	}

	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("wolf_heat");
	}

}
