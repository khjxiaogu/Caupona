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
