package com.teammoeg.caupona.data.recipes.baseconditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class BaseConditions {

	private static DataDeserializerRegistry<StewBaseCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<StewBaseCondition> CODEC=numbers.createCodec();
	public static final StreamCodec<ByteBuf,StewBaseCondition> STREAM_CODEC=numbers.createStreamCodec();
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

	public static StewBaseCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}
/*
	public static void write(StewBaseCondition e, FriendlyByteBuf buffer) {
		numbers.write(buffer, e);
	}*/

	public static void clearCache() {
		numbers.clearCache();
	}
}
