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

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.numbers.Add;
import com.teammoeg.caupona.data.recipes.numbers.ConstNumber;
import com.teammoeg.caupona.data.recipes.numbers.ItemIngredient;
import com.teammoeg.caupona.data.recipes.numbers.ItemTag;
import com.teammoeg.caupona.data.recipes.numbers.ItemType;
import com.teammoeg.caupona.data.recipes.numbers.NopNumber;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientNumberBuilder<T> {
	private IngredientConditionsBuilder<T> parent;
	private List<CookIngredients> types = new ArrayList<>();
	private Consumer<CookIngredients> fin;

	public IngredientNumberBuilder(IngredientConditionsBuilder<T> parent, Consumer<CookIngredients> fin) {
		super();
		this.parent = parent;
		this.fin = fin;
	}

	public IngredientNumberBuilder<T> of(float n) {
		return of(new ConstNumber(n));
	}

	public IngredientNumberBuilder<T> of(Ingredient i) {
		return of(new ItemIngredient(i));
	}
	public IngredientNumberBuilder<T> of(Ingredient i,String desc) {
		return of(new ItemIngredient(i,desc));
	}

	public IngredientNumberBuilder<T> of(ResourceLocation i) {
		return of(new ItemTag(i));
	}
	public IngredientNumberBuilder<T> of(TagKey<Item> i) {
		return of(i.location());
	}
	public IngredientNumberBuilder<T> of(Item i) {
		return of(new ItemType(i));
	}

	public IngredientNumberBuilder<T> of(CookIngredients sn) {
		types.add(sn);
		return this;
	}

	public IngredientNumberBuilder<T> plus(CookIngredients sn) {
		if (types.size() <= 0)
			return of(sn);
		CookIngredients sn2 = types.get(types.size() - 1);
		if (sn2 instanceof Add add) {
			add.add(sn);
		} else {
			List<CookIngredients> t2s = new ArrayList<>();
			t2s.add(sn2);
			t2s.add(sn);
			types.set(types.size() - 1, new Add(t2s));
		}
		return this;
	}

	public IngredientNumberBuilder<T> plus(float n) {
		return plus(new ConstNumber(n));
	}

	public IngredientNumberBuilder<T> plus(Ingredient i) {
		return plus(new ItemIngredient(i));
	}

	public IngredientNumberBuilder<T> plus(ResourceLocation i) {
		return plus(new ItemTag(i));
	}
	public IngredientNumberBuilder<T> plus(TagKey<Item> i) {
		return plus(i.location());
	}
	public IngredientNumberBuilder<T> plus(Item i) {
		return plus(new ItemType(i));
	}

	public IngredientNumberBuilder<T> nop() {
		return of(NopNumber.INSTANCE);
	}

	public IngredientConditionsBuilder<T> and() {
		if (!types.isEmpty()) {
			types.forEach(fin);
		}
		return parent;
	}
}