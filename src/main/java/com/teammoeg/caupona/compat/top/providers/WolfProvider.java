/*
 * Copyright (c) 2024 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

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
