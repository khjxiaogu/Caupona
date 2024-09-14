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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CountingTags extends IDataRecipe {
	public static Set<ResourceLocation> tags;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public List<ResourceLocation> tag;
	public static final MapCodec<CountingTags> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					Codec.list(ResourceLocation.CODEC).fieldOf("tags").forGetter(o->o.tag)
					).apply(t, CountingTags::new));
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public CountingTags() {
		tag = new ArrayList<>();
	}

	public CountingTags(List<ResourceLocation> tag) {
		super();
		this.tag = tag;
	}
/*
	public CountingTags(JsonObject jo) {
		if (jo.has("tag"))
			tag = ImmutableList.of(ResourceLocation.parse(jo.get("tag").getAsString()));
		else if (jo.has("tags"))
			tag = SerializeUtil.parseJsonElmList(jo.get("tags"), e -> new ResourceLocation(e.getAsString()));
	}*/

	public CountingTags(FriendlyByteBuf data) {
		tag = SerializeUtil.readList(data, FriendlyByteBuf::readResourceLocation);
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.<ResourceLocation>writeList2(data, tag, FriendlyByteBuf::writeResourceLocation);
	}

}
