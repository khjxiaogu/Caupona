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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FloatemTagStack {
	Set<ResourceLocation> tags;
	final ItemStack stack;
	float count;

	public FloatemTagStack(FloatemStack stack) {
		FoodValueRecipe fvr = FoodValueRecipe.recipes.get(stack.getItem());
		if (fvr == null)
			tags = stack.getTags().filter(CountingTags.tags::contains).collect(Collectors.toSet());
		else
			tags = fvr.getTags();
		this.stack = stack.getStack();
		this.count = stack.count;
	}

	public FloatemTagStack(ItemStack stack) {
		FoodValueRecipe fvr = FoodValueRecipe.recipes.get(stack.getItem());
		if (fvr == null) {
			tags = stack.getTags().map(TagKey::location).filter(CountingTags.tags::contains)
					.collect(Collectors.toSet());
		} else
			tags = fvr.getTags();
		this.stack = stack;

		this.count = stack.getCount();
	}

	public Set<ResourceLocation> getTags() {
		return tags;
	}

	public ItemStack getStack() {
		return stack;
	}

	public float getCount() {
		return count;
	}

	public static Map<ResourceLocation, Float> calculateTypes(Stream<FloatemTagStack> stacks) {
		Map<ResourceLocation, Float> map = new HashMap<>();
		stacks.forEach(e -> {
			float c = e.count;
			for (ResourceLocation tag : e.tags)
				map.merge(tag, c, Float::sum);
		});
		return map;
	}

	public Item getItem() {
		return stack.getItem();
	}
}
