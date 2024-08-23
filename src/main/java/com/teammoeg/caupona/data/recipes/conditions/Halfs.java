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

package com.teammoeg.caupona.data.recipes.conditions;

import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;

public class Halfs extends NumberedStewCondition {
	public static final MapCodec<Halfs> CODEC=NumberedStewCondition.createCodec(Halfs::new);

	public Halfs(CookIngredients number) {
		super(number);
	}

	@Override
	public boolean test(IPendingContext t, float n) {
		return n > t.getTotalItems() / 2;
	}


/*
	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
	}

	public Halfs(FriendlyByteBuf buffer) {
		super(buffer);
	}*/

	@Override
	public String getType() {
		return "half";
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.caupona.cond.half", number.getTranslation(p));
	}
	@Override
	public boolean isMajor() {
		return true;
	}
}
