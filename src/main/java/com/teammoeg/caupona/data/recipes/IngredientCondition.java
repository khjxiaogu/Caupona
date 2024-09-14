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

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.teammoeg.caupona.data.ITranlatable;

import net.minecraft.resources.ResourceLocation;

public interface IngredientCondition extends Predicate<IPendingContext>, /*Writeable,*/ ITranlatable {

	public String getType();

	public default Stream<CookIngredients> getAllNumbers() {
		return Stream.empty();
	};

	public default Stream<ResourceLocation> getTags() {
		return Stream.empty();
	};
	public default boolean isMajor() {
		return false;
	}
	public default boolean isExclusive() {
		return false;
	}
}
