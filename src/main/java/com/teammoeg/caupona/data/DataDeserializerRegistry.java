package com.teammoeg.caupona.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DataDeserializerRegistry<T> {
	private HashMap<String, Deserializer<? extends T>> deserializers = new HashMap<>();
	private List<Deserializer<? extends T>> byIdx=new ArrayList<>();
	private HashMap<Class<?>,String> nameOfClass=new HashMap<>();
	public <R extends T> void register(String name, Deserializer<R> des) {
		deserializers.put(name, des);
	}
	public <R extends T> void register(String name,Class<R> cls, MapCodec<R> rjson,
			StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) {
		Deserializer<R> des=new Deserializer<>(rjson,streamCodec,byIdx.size());
		register(name, des);
		byIdx.add(des);
		nameOfClass.put(cls, name);
	}
	public Deserializer<? extends T> getDeserializer(String type){
		return deserializers.get(type);
	}
	public T of(RegistryFriendlyByteBuf buffer) {
		return byIdx.get(buffer.readByte()).read(buffer);
	}
	public void clearCache() {
	}

	public MapCodec<? extends T> getCodec(String t) {
		Deserializer<? extends T> des=getDeserializer(t);
		if(des==null)
			return null;
		return des.fromJson;
	}
	public byte getId(T t){
		return (byte) deserializers.get(nameOfClass.get(t.getClass())).getId();
	}
	public StreamCodec<RegistryFriendlyByteBuf,? extends T> getStreamCodec(byte t) {
		Deserializer<? extends T> des=byIdx.get(t);
		if(des==null)
			return null;
		return des.fromPacket;
	}
	public Codec<T> createCodec(){
		return Codec.STRING.dispatch("type", t->t==null?null:nameOfClass.get(t.getClass()), t->getCodec(t));
	}
	public StreamCodec<RegistryFriendlyByteBuf,T> createStreamCodec(){
		return ByteBufCodecs.BYTE.mapStream((Function<RegistryFriendlyByteBuf,ByteBuf>)t->t).dispatch(t->getId(t),t->getStreamCodec(t));

	}

}
