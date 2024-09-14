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
