package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.blocks.foods.BowlBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;
import com.teammoeg.caupona.util.Utils;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class BowlProvider implements IProbeInfoProvider {

	public BowlProvider() {
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof BowlBlockEntity entity) {
			
			//info.tankSimple(1250, .getFluid());
			if(!entity.getInternal().isEmpty()) {
				FluidStack extracted=Utils.extractFluid(entity.getInternal());
				if(!extracted.isEmpty())
					info.tankSimple(250,extracted, info.defaultProgressStyle().showText(false).height(16).width(16));
			}
			
	
		}
	}

	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("bowl");
	}

}
