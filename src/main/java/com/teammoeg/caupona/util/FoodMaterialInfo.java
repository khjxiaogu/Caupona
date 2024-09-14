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

import java.util.function.Consumer;

import net.minecraft.world.food.FoodProperties;

public class FoodMaterialInfo {
	public String name;
	public float composite;
	public FoodProperties.Builder food;
	public FoodMaterialInfo(String name, int heal, float sat) {
		super();
		this.name = name;
		food=new FoodProperties.Builder();
		food.nutrition(heal);
		food.saturationModifier(sat);
	}
	public FoodMaterialInfo(String name, float composite) {
		super();
		this.name = name;
		this.composite = composite;
	}
	public FoodMaterialInfo(String name, int heal, float sat, float composite) {
		this(name,heal,sat);
		this.composite = composite;
	}
	public FoodMaterialInfo food(Consumer<FoodProperties.Builder> cons) {
		cons.accept(food);
		return this;
	}
}
