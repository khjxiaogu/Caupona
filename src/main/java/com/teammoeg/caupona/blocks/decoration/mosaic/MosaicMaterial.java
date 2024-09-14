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

package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

public enum MosaicMaterial implements StringRepresentable{
	brick("t"),
	basalt("b"),
	pumice("p");
	public final String shortName;
	private final Lazy<Item> tell;
	private MosaicMaterial(String shortName) {
		this.shortName = shortName;
		this.tell=Utils.itemSupplier(name()+"_tesserae");
	}

	private Item getTesserae() {
		return tell.get();
	}
	private static Map<Item,MosaicMaterial> materials;
	public static MosaicMaterial fromItem(ItemStack is) {
		if(materials==null) {
			materials=ImmutableMap.of(brick.getTesserae(),brick,basalt.getTesserae(),basalt,pumice.getTesserae(),pumice);
		}
		return materials.get(is.getItem());
	}
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
