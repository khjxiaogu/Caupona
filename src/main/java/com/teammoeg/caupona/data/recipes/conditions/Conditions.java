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

package com.teammoeg.caupona.data.recipes.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Conditions {
	private static DataDeserializerRegistry<IngredientCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<IngredientCondition> CODEC=numbers.createCodec();
	public static final StreamCodec<RegistryFriendlyByteBuf,IngredientCondition> STREAM_CODEC=numbers.createStreamCodec();
	static {
		register("half",Halfs.class, Halfs.CODEC);
		register("mainly",Mainly.class, Mainly.CODEC);
		register("contains",Must.class, Must.CODEC);
		register("mainlyOf",MainlyOfType.class, MainlyOfType.CODEC);
		register("only",Only.class, Only.CODEC);
		
	}
	public static void register(String name, Deserializer<IngredientCondition> des) {
		numbers.register(name, des);
	}

	public static <R extends IngredientCondition> void register(String name,Class<R> cls, MapCodec<R> rjson) {
		numbers.register(name,cls, rjson, SerializeUtil.toStreamCodec(rjson));
	}
	
/*
	public static IngredientCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}*/

	public static void checkConditions(Collection<IngredientCondition> allow) {
		if(allow==null)return;
		boolean foundMajor=false;
		Set<Class<? extends IngredientCondition>> conts=new HashSet<>();
		
		for(IngredientCondition c:allow) {
			if(c.isMajor()) {
				if(foundMajor)
					throw new InvalidRecipeException("There must be less than one major condition. (Current: "+c.getType()+")");
				foundMajor=true;
			}else if(c.isExclusive()) {
				if(conts.contains(c.getClass()))
					throw new InvalidRecipeException("There must be less than one "+c.getType()+" condition.");
				conts.add(c.getClass());
			}
		}
	}


	public static void clearCache() {
		numbers.clearCache();
	}
}
