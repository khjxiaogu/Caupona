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

package com.teammoeg.caupona.util;

import java.util.HashMap;
import java.util.Map;

import com.teammoeg.caupona.CPMain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record FuelType (TagKey<Item> it,String modelLayer,String cold_ash,String hot_ash){
	private static final Map<ResourceLocation,FuelType> types=new HashMap<>();
	public static final FuelType WOODS=register(new FuelType("fuel/woods","FirewoodFuel","ColdAsh","HotAsh"));
	public static final FuelType CHARCOAL=register(new FuelType("fuel/charcoals","CharcoalFuel","ColdAsh","HotAsh"));
	public static final FuelType FOSSIL=register(new FuelType("fuel/fossil","CharcoalFuel","ColdAsh","HotAsh"));
	public static final FuelType LAVA=register(new FuelType("fuel/lava","LavaBucketFuel","ColdLavaFuel","LavaFuel"));
	public static final FuelType OTHER=register(new FuelType("fuel/others",null,null,null));
	


	public static final FuelType register(FuelType type) {
		//FuelType orig=types.get(type.it.location());
		types.put(type.it.location(), type);
		return type;
	}


	public FuelType(String tagname,String modelLayer, String hot_ash, String cold_ash) {
		this(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, tagname),modelLayer,hot_ash,cold_ash);
	}
	public FuelType(ResourceLocation tag,String modelLayer, String hot_ash, String cold_ash) {
		this(ItemTags.create(tag),modelLayer,hot_ash,cold_ash);
	}

	public static FuelType getType(ItemStack is) {
		if(is.isEmpty())
			return FuelType.OTHER;
		for (FuelType ft : types.values()) {
			if (is.is(ft.it))
				return ft;
		}
		return FuelType.OTHER;
	}
	public static FuelType parse(String toParse) {
		return types.getOrDefault(ResourceLocation.parse(toParse),FuelType.OTHER);
	}
	public String serialize() {
		return it.location().toString();
	}


	public TagKey<Item> it() {
		return it;
	}


	public String modelLayer() {
		return modelLayer;
	}


	public String cold_ash() {
		return cold_ash;
	}


	public String hot_ash() {
		return hot_ash;
	}
}
