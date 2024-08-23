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

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DissolveRecipe extends IDataRecipe {
	public static List<RecipeHolder<DissolveRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public Ingredient item;
	public int time;
	public static final MapCodec<DissolveRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					Ingredient.CODEC_NONEMPTY.fieldOf("item").forGetter(o->o.item),
					Codec.INT.fieldOf("time").forGetter(o->o.time)
					).apply(t, DissolveRecipe::new));
	public DissolveRecipe(Ingredient item, int time) {
		this.item = item;
		this.time = time;
	}
/*
	public DissolveRecipe(JsonObject jo) {
		item = Ingredient.fromJson(jo.get("item"),true);
		time = jo.get("time").getAsInt();
	}
*/
	public boolean test(ItemStack is) {
		return item.test(is);
	}
/*
	public DissolveRecipe(FriendlyByteBuf data) {
		item = Ingredient.fromNetwork(data);
		time = data.readVarInt();
	}

	public void write(FriendlyByteBuf data) {
		item.toNetwork(data);
		data.writeVarInt(time);
	}
*/

}
