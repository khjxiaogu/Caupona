package com.teammoeg.caupona.compat.top.providers;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.compat.top.TOPRegister;
import com.teammoeg.caupona.util.IInfinitable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class InfProvider  implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof IInfinitable entity) {
			if(player.isShiftKeyDown()&&entity.isInfinite()) {
				info.item(new ItemStack(CPItems.chronoconis));
			}
		}
	}


	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("kitchen_stove");
	}

}
