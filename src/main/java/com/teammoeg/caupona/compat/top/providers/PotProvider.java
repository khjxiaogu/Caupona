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
