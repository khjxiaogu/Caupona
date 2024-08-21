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

package com.teammoeg.caupona.data.recipes.numbers;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemIngredient implements CookIngredients {
	public static final MapCodec<ItemIngredient> CODEC=
		RecordCodecBuilder.mapCodec(t->t.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(o->o.i),
			Codec.STRING.optionalFieldOf("translation").forGetter(o->Optional.ofNullable(o.translation))).apply(t, ItemIngredient::new));
	Ingredient i;
	String translation="";

	public ItemIngredient(Ingredient i) {
		super();
		this.i = i;
	}
	public ItemIngredient(Ingredient i, Optional<String> translation) {
		super();
		this.i = i;
		this.translation = translation.orElse(null);
	}
	public ItemIngredient(Ingredient i, String translation) {
		super();
		this.i = i;
		this.translation = translation;
	}

	@Override
	public Float apply(IPendingContext t) {
		return t.getOfItem(i);
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return i.test(stack.getStack());
	}

	/*@Override
	public void write(FriendlyByteBuf buffer) {
		Ingredient.CONTENTS_STREAM_CODEC.encode(null, i);
		buffer.writeUtf(translation);
	}

	public ItemIngredient(FriendlyByteBuf buffer) {
		i = Ingredient.(buffer);
		translation=buffer.readUtf();
	}*/

	@Override
	public Stream<CookIngredients> getItemRelated() {
		return Stream.of(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((i == null) ? 0 : i.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ItemIngredient))
			return false;
		ItemIngredient other = (ItemIngredient) obj;
		if (i == null) {
			if (other.i != null)
				return false;
		} else if (!i.equals(other.i))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslationOrElse(translation, translation);
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return Arrays.stream(i.getItems());
	}

}
