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

package com.teammoeg.caupona.data.recipes;

import java.util.List;
import java.util.function.Predicate;

import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class IPendingContext {

	protected List<FloatemTagStack> items;
	protected float totalItems;
	private ResultCachingMap<CookIngredients, Float> numbers = new ResultCachingMap<>(e -> e.apply(this));
	private ResultCachingMap<IngredientCondition, Boolean> results = new ResultCachingMap<>(e -> e.test(this));

	public IPendingContext() {
		super();
	}

	public float compute(CookIngredients sn) {
		return numbers.compute(sn);
	}

	public boolean compute(IngredientCondition sc) {
		return results.compute(sc);
	}

	public float getOfType(ResourceLocation rl) {
		return (float) items.stream().filter(e -> e.getTags().contains(rl)).mapToDouble(FloatemTagStack::getCount)
				.sum();
	}

	public float getOfItem(Predicate<ItemStack> pred) {
		for (FloatemTagStack fs : items)
			if (pred.test(fs.getStack()))
				return fs.getCount();
		return 0f;
	}

	public float getTotalItems() {
		return totalItems;
	}

	public List<FloatemTagStack> getItems() {
		return items;
	}

}