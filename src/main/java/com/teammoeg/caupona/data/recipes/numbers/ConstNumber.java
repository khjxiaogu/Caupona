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

package com.teammoeg.caupona.data.recipes.numbers;

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ConstNumber implements CookIngredients {
	float n;
	public static final MapCodec<ConstNumber> CODEC=RecordCodecBuilder.mapCodec(t->t.group(Codec.FLOAT.fieldOf("num").forGetter(o->o.n)).apply(t, ConstNumber::new));
	public ConstNumber(JsonElement num) {
		if (num.isJsonPrimitive())
			n = num.getAsFloat();
		else
			n = num.getAsJsonObject().get("num").getAsFloat();
	}

	public ConstNumber(float n) {
		super();
		this.n = n;
	}

	@Override
	public Float apply(IPendingContext t) {
		return n;
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return false;
	}

	/*@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeFloat(n);
	}

	public ConstNumber(FriendlyByteBuf buffer) {
		n = buffer.readFloat();
	}*/


	@Override
	public Stream<CookIngredients> getItemRelated() {
		return Stream.empty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(n);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ConstNumber))
			return false;
		ConstNumber other = (ConstNumber) obj;
		if (Float.floatToIntBits(n) != Float.floatToIntBits(other.n))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return String.valueOf(n);
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return Stream.of();
	}

}
