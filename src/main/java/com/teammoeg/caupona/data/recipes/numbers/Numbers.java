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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Numbers{
	private static DataDeserializerRegistry<CookIngredients> numbers=new DataDeserializerRegistry<>();
	public static final Codec<CookIngredients> CODEC=numbers.createCodec();
	public static final StreamCodec<RegistryFriendlyByteBuf, CookIngredients> STREAM_CODEC=numbers.createStreamCodec();
	static {
		register("add", Add.class, Add.CODEC);
		register("ingredient", ItemIngredient.class, ItemIngredient.CODEC);
		register("item", ItemType.class, ItemType.CODEC);
		register("tag", ItemTag.class, ItemTag.CODEC);
		register("nop", NopNumber.class, NopNumber.CODEC);
		register("const", ConstNumber.class, ConstNumber.CODEC);
	}
	private Numbers(){
		
	}
	public static <T extends CookIngredients> void register(String name,Class<T> cls, MapCodec<T> rjson,
			StreamCodec<RegistryFriendlyByteBuf, T> rpacket) {
		numbers.register(name, cls, rjson, rpacket);
	}
	public static <T extends CookIngredients> void register(String name,Class<T> cls, MapCodec<T> rjson) {
		numbers.register(name, cls, rjson, SerializeUtil.toStreamCodec(rjson));
	}
//	public static CookIngredients of(FriendlyByteBuf buffer) {
//		return numbers.of(buffer);
//	}
	public static void clearCache() {
		numbers.clearCache();
	}


}
