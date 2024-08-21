/*
 * Copyright (c) 2022 TeamMoeg
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

import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.ITranlatable;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public interface StewBaseCondition extends BiFunction<Fluid, Fluid, Integer>,
		Predicate<Fluid>, ITranlatable {

	public String getType();

	public boolean test(Fluid f);
}
