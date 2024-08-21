package com.teammoeg.caupona.data.recipes.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Conditions {
	private static DataDeserializerRegistry<IngredientCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<IngredientCondition> CODEC=numbers.createCodec();
	public static final StreamCodec<ByteBuf,IngredientCondition> STREAM_CODEC=numbers.createStreamCodec();
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
	

	public static IngredientCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}

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
