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

package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.CPMain;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Tool class for serialize data, packets etc
 */
public class SerializeUtil {
	

	private SerializeUtil() {

	}

	public static <T> Optional<T> readOptional(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (buffer.readBoolean())
			return Optional.ofNullable(func.apply(buffer));
		return Optional.empty();
	}

	public static <T> void writeOptional2(FriendlyByteBuf buffer, T data, BiConsumer<FriendlyByteBuf, T> func) {
		writeOptional(buffer, data, (a, b) -> func.accept(b, a));
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, T data, BiConsumer<T, FriendlyByteBuf> func) {
		writeOptional(buffer, Optional.ofNullable(data), func);
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, Optional<T> data,
			BiConsumer<T, FriendlyByteBuf> func) {
		if (data.isPresent()) {
			buffer.writeBoolean(true);
			func.accept(data.get(), buffer);
			return;
		}
		buffer.writeBoolean(false);
	}

	public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (!buffer.readBoolean())
			return null;
		int cnt = buffer.readVarInt();
		List<T> nums = new ArrayList<>(cnt);
		for (int i = 0; i < cnt; i++)
			nums.add(func.apply(buffer));
		return nums;
	}

	public static <T> void writeList(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<T, FriendlyByteBuf> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(e, buffer));
	}

	public static <T> void writeList2(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<FriendlyByteBuf, T> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(buffer, e));
	}

	public static <T> List<T> parseJsonList(JsonElement elm, Function<JsonObject, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(JsonElement::getAsJsonObject)
					.map(mapper).collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> List<T> parseJsonElmList(JsonElement elm, Function<JsonElement, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(mapper)
					.collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> JsonArray toJsonList(Collection<T> li, Function<T, JsonElement> mapper) {
		JsonArray ja = new JsonArray();
		li.stream().map(mapper).forEach(ja::add);
		return ja;
	}

	public static <T> ListTag toNBTList(Collection<T> stacks, Function<T, Tag> mapper) {
		ListTag nbt = new ListTag();
		stacks.stream().map(mapper).forEach(nbt::add);
		return nbt;
	}
	public static FluidStack readFluidStack(JsonElement jsonIn) {
		if(jsonIn==null)return null;
		return FluidStack.CODEC.decode(JsonOps.INSTANCE,jsonIn).result().map(Pair::getFirst).orElse(FluidStack.EMPTY);
	}
    public static <K, V> void writeMap(FriendlyByteBuf buffer, Map<K, V> elms, BiConsumer<K, FriendlyByteBuf> keywriter, BiConsumer<V, FriendlyByteBuf> valuewriter) {
        writeList(buffer, elms.entrySet(), (p, b) -> {
            keywriter.accept(p.getKey(), b);
            valuewriter.accept(p.getValue(), b);
        });
    }
	public static <T extends Enum> StreamCodec<ByteBuf,T> createEnumStreamCodec(T[] values) {
		return ByteBufCodecs.BYTE.map(n->values[n], v->(byte)v.ordinal());
	}
    public static <K, V> Map<K, V> readMap(FriendlyByteBuf buffer, Map<K, V> map, Function<FriendlyByteBuf, K> keyreader, Function<FriendlyByteBuf, V> valuereader) {
        map.clear();
        if (!buffer.readBoolean())
            return map;
        int cnt = buffer.readVarInt();
        for (int i = 0; i < cnt; i++)
            map.put(keyreader.apply(buffer), valuereader.apply(buffer));
        return map;
    }
    public static <T> void writeCodec(FriendlyByteBuf pb, Codec<T> codec, T obj) {
    	DataResult<Object> ob=codec.encodeStart(DataOps.COMPRESSED, obj);
    	Optional<Object> ret=ob.resultOrPartial(CPMain.logger::error);
    	ObjectWriter.writeObject(pb,ret.get());
    }
    public static <T> T readCodec(FriendlyByteBuf pb, Codec<T> codec) {
    	DataResult<Pair<T, Object>> ob=codec.decode(DataOps.COMPRESSED, ObjectWriter.readObject(pb));
    	Optional<Pair<T, Object>> ret=ob.resultOrPartial(CPMain.logger::error);
    	return ret.get().getFirst();
    }

	public static <K, V> Map<K, V> readEntry(FriendlyByteBuf buffer, Map<K, V> map, BiConsumer<FriendlyByteBuf, BiConsumer<K, V>> reader) {
		map.clear();
		int cnt = buffer.readVarInt();
		for (int i = 0; i < cnt; i++)
			reader.accept(buffer, map::put);
		return map;
	}
	public static <K, V> void writeEntry(FriendlyByteBuf buffer, Map<K, V> elms, BiConsumer<Map.Entry<K, V>, FriendlyByteBuf> func) {
		buffer.writeVarInt(elms.size());
		elms.entrySet().forEach(e -> func.accept(e, buffer));
	}
	public static <F extends FriendlyByteBuf,V> StreamCodec<F,V> toStreamCodec(Codec<V> codec){
		return StreamCodec.of((b,v)->{
			writeCodec(b,codec,v);
		},(b)->readCodec(b,codec));
	}
	public static <F extends FriendlyByteBuf,V> StreamCodec<F,V> toStreamCodec(MapCodec<V> codec){
		return StreamCodec.of((b,v)->{
			writeCodec(b,codec.codec(),v);
		},(b)->readCodec(b,codec.codec()));
	}
	public static <T> Codec<T> idOrKey(Registry<T> registry){
		Codec<T> byKeyCodec=registry.byNameCodec();
		return new Codec<T>() {

			@Override
			public <O> DataResult<O> encode(T input, DynamicOps<O> ops, O prefix) {
				if(ops.compressMaps())
					return DataResult.success(ops.createInt(registry.getId(input)));
				return byKeyCodec.encode(input, ops, prefix);
			}

			@Override
			public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
				if(ops.compressMaps())
					return ops.getNumberValue(input).map(p->Pair.of(registry.byId(p.intValue()),input));
				return byKeyCodec.decode(ops,input);
			}
			public String toString() {
				return "IdOrKeyCodec[registry="+registry+"]";
			}
		};
	}
}
