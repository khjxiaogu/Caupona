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

import java.util.Objects;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemType implements CookIngredients {
	Item type;
	public static final MapCodec<ItemType> CODEC=
		RecordCodecBuilder.mapCodec(t->t.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(o->o.type)).apply(t, ItemType::new));

	public ItemType(Item type) {
		super();
		this.type = type;
	}

	@Override
	public Float apply(IPendingContext t) {
		if (type == null)
			return 0F;
		return t.getOfItem(i -> i.getItem().equals(type));
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return stack.getItem().equals(type);
	}

/*	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeById(BuiltInRegistries.ITEM::getId,type);
	}

	public ItemType(FriendlyByteBuf buffer) {
		type = buffer.readById(BuiltInRegistries.ITEM::byId);

	}*/

	@Override
	public Stream<CookIngredients> getItemRelated() {
		return Stream.of(this);
	}



	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ItemType other = (ItemType) obj;
		return Objects.equals(type, other.type);
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.empty();
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation(type.getDescriptionId());
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return Stream.of(new ItemStack(type));
	}
}
