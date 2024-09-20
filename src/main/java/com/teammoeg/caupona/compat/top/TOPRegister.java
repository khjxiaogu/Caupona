package com.teammoeg.caupona.compat.top;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.compat.top.providers.*;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.resources.ResourceLocation;

public class TOPRegister {

	private TOPRegister() {
	}
	public static Object register(ITheOneProbe top) {
		top.registerProvider(new BathProvider());
		top.registerProvider(new BowlProvider());
		top.registerProvider(new DoliumProvider());
		top.registerProvider(new FumaroleProvider());
		top.registerProvider(new GravyBoatProvider());
		top.registerProvider(new LoafProvider());
		top.registerProvider(new PotProvider());
		top.registerProvider(new StoveProvider());
		top.registerProvider(new WolfProvider());
		top.registerProvider(new InfProvider());
		return null;
	}
	public static ResourceLocation idForBlock(String name) {
		return CPMain.rl(name+"_block_info");
	}
}
