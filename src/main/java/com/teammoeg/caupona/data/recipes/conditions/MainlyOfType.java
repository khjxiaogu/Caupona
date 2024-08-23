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

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.data.recipes.numbers.Numbers;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.resources.ResourceLocation;

public class MainlyOfType extends NumberedStewCondition {
	private final ResourceLocation type;
	public static final MapCodec<MainlyOfType> CODEC=RecordCodecBuilder.mapCodec(t->t.group(Numbers.CODEC.fieldOf("number").forGetter(o->o.number),ResourceLocation.CODEC.fieldOf("tag").forGetter(o->o.type)).apply(t, MainlyOfType::new));

	public MainlyOfType(CookIngredients obj, ResourceLocation type) {
		super(obj);
		this.type = type;
	}

	@Override
	public boolean test(IPendingContext t, float n) {
		float thistype = t.getOfType(type);
		if (n < thistype / 3)
			return false;
		return FloatemTagStack
				.calculateTypes(
						t.getItems().stream().filter(e -> e.getTags().contains(type)).filter(e -> !number.fits(e)))
				.values().stream().allMatch(e -> e < n);
	}

/*
	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeResourceLocation(type);
	}

	public MainlyOfType(FriendlyByteBuf buffer) {
		super(buffer);
		type = buffer.readResourceLocation();
	}*/

	@Override
	public String getType() {
		return "mainlyOf";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MainlyOfType))
			return false;
		if (!super.equals(obj))
			return false;
		MainlyOfType other = (MainlyOfType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.concat(super.getTags(), Stream.of(type));
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.caupona.cond.mainlyof", number.getTranslation(p),
				p.getTranslation("tag." + this.type.toString().replaceAll("[:/]", ".")));
	}
}
