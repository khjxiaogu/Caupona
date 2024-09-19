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

package com.teammoeg.caupona.components;

import java.util.List;

import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

public interface IFoodInfo {
	List<FloatemStack> getStacks();
	int getHealing();
	float getSaturation();
	FoodProperties.Builder getFood(int extraHealing,int extraSaturation);
	default FoodProperties getFood() {
		return getFood(0,0).usingConvertsTo(Items.BOWL).build();
	}
	Fluid getBase();
	List<PossibleEffect> getEffects();
	default float getDensity() {
		return getStacks().stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}
}
