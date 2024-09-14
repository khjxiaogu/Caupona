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

package com.teammoeg.caupona.data.recipes.baseconditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class BaseConditions {

	private static DataDeserializerRegistry<StewBaseCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<StewBaseCondition> CODEC=numbers.createCodec();
	public static final StreamCodec<RegistryFriendlyByteBuf, StewBaseCondition> STREAM_CODEC=numbers.createStreamCodec();
	static {
		register("tag", FluidTag.class, FluidTag.CODEC);
		register("fluid", FluidType.class, FluidType.CODEC);
		register("fluid_type", FluidTypeType.class, FluidTypeType.CODEC);
	}
	public static void register(String name, Deserializer<StewBaseCondition> des) {
		numbers.register(name, des);
	}

	public static <R extends StewBaseCondition> void register(String name,Class<R> cls, MapCodec<R> rjson) {
		numbers.register(name, cls, rjson, SerializeUtil.toStreamCodec(rjson));
	}

//	public static StewBaseCondition of(FriendlyByteBuf buffer) {
//		return numbers.of(buffer);
//	}
/*
	public static void write(StewBaseCondition e, FriendlyByteBuf buffer) {
		numbers.write(buffer, e);
	}*/

	public static void clearCache() {
		numbers.clearCache();
	}
}
