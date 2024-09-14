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

import java.util.List;

import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.conditions.Halfs;
import com.teammoeg.caupona.data.recipes.conditions.Mainly;
import com.teammoeg.caupona.data.recipes.conditions.MainlyOfType;
import com.teammoeg.caupona.data.recipes.conditions.Must;
import com.teammoeg.caupona.data.recipes.conditions.Only;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class IngredientConditionsBuilder<T> {
	private T parent;
	private List<IngredientCondition> li, al, dy;

	public IngredientConditionsBuilder(T parent, List<IngredientCondition> cr, List<IngredientCondition> al,
			List<IngredientCondition> dy) {
		super();
		this.parent = parent;
		this.li = cr;
		this.al = al;
		this.dy = dy;
	}

	public IngredientNumberBuilder<T> half() {
		return new IngredientNumberBuilder<T>(this, this::makeHalf);
	}

	private void makeHalf(CookIngredients sn) {
		li.add(new Halfs(sn));
	}

	public IngredientNumberBuilder<T> typeMainly(ResourceLocation rs) {
		return new IngredientNumberBuilder<T>(this, sn -> li.add(new MainlyOfType(sn, rs)));
	}
	public IngredientNumberBuilder<T> typeMainly(TagKey<Item> rs) {
		return typeMainly(rs.location());
	}
	public IngredientNumberBuilder<T> mainly() {
		return new IngredientNumberBuilder<T>(this, this::makeMainly);
	}

	private void makeMainly(CookIngredients sn) {
		li.add(new Mainly(sn));
	}

	public IngredientNumberBuilder<T> any() {
		return new IngredientNumberBuilder<T>(this, this::makeMust);
	}

	public IngredientNumberBuilder<T> only() {
		return new IngredientNumberBuilder<T>(this, this::makeOnly);
	}

	public IngredientConditionsBuilder<T> require() {
		return new IngredientConditionsBuilder<T>(parent, al, al, dy);
	}

	public IngredientConditionsBuilder<T> not() {
		return new IngredientConditionsBuilder<T>(parent, dy, al, dy);
	}

	private void makeMust(CookIngredients sn) {
		li.add(new Must(sn));
	}

	private void makeOnly(CookIngredients sn) {
		li.add(new Only(sn));
	}

	public T then() {
		return parent;
	}
}