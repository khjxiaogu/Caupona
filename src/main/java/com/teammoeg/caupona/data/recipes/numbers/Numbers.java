package com.teammoeg.caupona.data.recipes.numbers;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Numbers{
	private static DataDeserializerRegistry<CookIngredients> numbers=new DataDeserializerRegistry<>();
	public static final Codec<CookIngredients> CODEC=numbers.createCodec();
	public static final StreamCodec<ByteBuf,CookIngredients> STREAM_CODEC=numbers.createStreamCodec();
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
			StreamCodec<FriendlyByteBuf, T> rpacket) {
		numbers.register(name, cls, rjson, rpacket);
	}
	public static <T extends CookIngredients> void register(String name,Class<T> cls, MapCodec<T> rjson) {
		numbers.register(name, cls, rjson, SerializeUtil.toStreamCodec(rjson));
	}
	public static CookIngredients of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}
	public static void clearCache() {
		numbers.clearCache();
	}


}
