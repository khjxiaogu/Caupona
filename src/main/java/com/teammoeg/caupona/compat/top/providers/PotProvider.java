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

import java.util.ArrayList;
import java.util.List;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PotProvider  implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof StewPotBlockEntity entity) {
			//info.tankSimple(1250, .getFluid());
			info.tankHandler(entity.getTank());
			if(player.isShiftKeyDown()) {
				ItemStackHandler inventory=entity.getInv();
				List<ItemStack> inputs=new ArrayList<>();
				for(int i=0;i<inventory.getSlots();i++) {
					if(!inventory.getStackInSlot(i).isEmpty())
					inputs.add(inventory.getStackInSlot(i));
				}
				if(!inputs.isEmpty()) {
					IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(0));
					inputs.forEach(layout::item);
				}
			}
			if(entity.rsstate) {
				info.item(new ItemStack(Items.REDSTONE_TORCH));
			}
			info.progress(entity.process, entity.processMax);
			if(entity.processMax>0) {
				
				
				if(player.isShiftKeyDown()){
					if(entity.output!=null) {
						info.tankSimple(entity.output.getAmount(),entity.output, info.defaultProgressStyle().showText(false).height(16).width(16));
					}
				}
			}

		}
	}


	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("stew_pot");
	}

}
