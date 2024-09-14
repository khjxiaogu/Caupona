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
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NopNumber implements CookIngredients {
	public static final NopNumber INSTANCE = new NopNumber();
	public static final MapCodec<NopNumber> CODEC=MapCodec.unit(INSTANCE);
	/**
	 * @param elm
	 */
	public static NopNumber of(JsonElement elm) {
		return INSTANCE;
	}

	/**
	 * @param elm
	 */
	public NopNumber(JsonElement elm) {
	}

	public NopNumber() {
	}

	@Override
	public Float apply(IPendingContext t) {
		return 0F;
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return false;
	}

/*	@Override
	public void write(FriendlyByteBuf buffer) {
	}*/

	/**
	 * @param buffer
	 */
	public static NopNumber of(FriendlyByteBuf buffer) {
		return INSTANCE;
	}

	@Override
	public Stream<CookIngredients> getItemRelated() {
		return Stream.empty();
	}

	@Override
	public int hashCode() {
		return NopNumber.class.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NopNumber;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return "0";
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return Stream.empty();
	}

}
