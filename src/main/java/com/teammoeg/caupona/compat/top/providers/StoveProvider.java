package com.teammoeg.caupona.compat.top.providers;

import java.util.ArrayList;
import java.util.List;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StoveProvider  implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof KitchenStoveBlockEntity entity) {
			//info.tankSimple(1250, .getFluid());
			if(player.isShiftKeyDown()) {
				Container inventory=entity;
				List<ItemStack> inputs=new ArrayList<>();
				for(int i=0;i<inventory.getContainerSize();i++) {
					if(!inventory.getItem(i).isEmpty())
					inputs.add(
					inventory.getItem(i));
				}
				if(!inputs.isEmpty()) {
					IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(0));
					inputs.forEach(layout::item);
				}
			}
			info.progress(entity.process, entity.processMax);

		}
	}


	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("kitchen_stove");
	}

}
