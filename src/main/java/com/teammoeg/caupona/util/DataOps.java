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

package com.teammoeg.caupona.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class DataOps implements DynamicOps<Object> {
	private static class LBuilder implements ListBuilder<Object> {
		private DataResult<List<Object>> list = DataResult.success(new ArrayList<>());
		DataOps ops;

		public LBuilder(final DataOps ops) {
			this.ops = ops;
		}

		@Override
		public LBuilder add(final DataResult<Object> value) {
			list.apply2stable(List::add, value);
			return this;
		}

		@Override
		public LBuilder add(final Object value) {
			list.map(t -> t.add(value));
			return this;
		}

		@Override
		public DataResult<Object> build(final Object prefix) {
			final DataResult<Object> result = list.flatMap(b -> ops.mergeToList(prefix, b));
			list = DataResult.success(new ArrayList<>(), Lifecycle.stable());
			return result;
		}

		@Override
		public LBuilder mapError(final UnaryOperator<String> onError) {
			list = list.mapError(onError);
			return this;
		}

		@Override
		public DataOps ops() {
			return ops;
		}

		@Override
		public LBuilder withErrorsFrom(final DataResult<?> result) {
			list = list.flatMap(r -> result.map(v -> r));
			return this;
		}
	}

	private static class MBuilder implements RecordBuilder<Object> {
		DataOps ops;
		DataResult<Map<Object, Object>> map;

		public MBuilder(DataOps ops) {
			super();
			this.ops = ops;
			map = DataResult.success(new HashMap<>());
		}

		@Override
		public MBuilder add(DataResult<Object> key, DataResult<Object> value) {
			map.ap(key.apply2stable((k, v) -> b -> b.put(k, v), value));
			return this;
		}

		@Override
		public MBuilder add(Object key, DataResult<Object> value) {
			map.apply2stable((o, r) -> o.put(key, r), value);
			return this;
		}

		@Override
		public MBuilder add(Object key, Object value) {
			map.map(o -> o.put(key, value));
			return this;
		}

		@Override
		public DataResult<Object> build(Object prefix) {
			final DataResult<Object> result = map.flatMap(b -> ops.mergeToMap(prefix, b));
			map = DataResult.success(new HashMap<>(), Lifecycle.stable());
			return result;
		}

		@Override
		public MBuilder mapError(final UnaryOperator<String> onError) {
			map = map.mapError(onError);
			return this;
		}

		@Override
		public DynamicOps<Object> ops() {
			return ops;
		}

		@Override
		public MBuilder setLifecycle(final Lifecycle lifecycle) {
			map = map.setLifecycle(lifecycle);
			return this;
		}

		@Override
		public MBuilder withErrorsFrom(final DataResult<?> result) {
			map.flatMap(v -> result.map(r -> v));
			return this;
		}
	}

	private static class MLike implements MapLike<Object> {
		Map<Object, Object> map;

		public MLike(Map<Object, Object> map) {
			super();
			this.map = map;
		}

		@Override
		public Stream<Pair<Object, Object>> entries() {
			return map.entrySet().stream().map(t -> Pair.of(t.getKey(), t.getValue()));
		}

		@Override
		public Object get(Object key) {
			return map.get(key);
		}

		@Override
		public Object get(String key) {
			return map.get(key);
		}

		@Override
		public String toString() {
			return "MLike [" + map + "]";
		}
	}

	public static final DataOps INSTANCE = new DataOps(false);

	public static final DataOps COMPRESSED = new DataOps(true);
	
	public static record RegisterFriendlyDataOps(DataOps op,RegistryAccess registryAccess,net.neoforged.neoforge.network.connection.ConnectionType connectionType)  implements DynamicOps<Object>, IRegistryAccessable{
		public RegisterFriendlyDataOps(DataOps op,RegistryFriendlyByteBuf buf) {
			this(op,buf.registryAccess(),buf.getConnectionType());
		}

		public Object createByte(byte value) {
			return op.createByte(value);
		}

		public Object createShort(short value) {
			return op.createShort(value);
		}

		public Object createInt(int value) {
			return op.createInt(value);
		}

		public Object createLong(long value) {
			return op.createLong(value);
		}

		public Object createFloat(float value) {
			return op.createFloat(value);
		}

		public int hashCode() {
			return op.hashCode();
		}

		public Object createDouble(double value) {
			return op.createDouble(value);
		}

		public DataResult<Boolean> getBooleanValue(Object input) {
			return op.getBooleanValue(input);
		}

		public Object createBoolean(boolean value) {
			return op.createBoolean(value);
		}

		public boolean equals(Object obj) {
			return op.equals(obj);
		}

		public boolean compressMaps() {
			return op.compressMaps();
		}

		public <U> U convertTo(DynamicOps<U> outOps, Object input) {
			return op.convertTo(outOps, input);
		}

		public Object createList(Stream<Object> input) {
			return op.createList(input);
		}

		public Object createMap(Map<Object, Object> map) {
			return op.createMap(map);
		}

		public Object createMap(Stream<Pair<Object, Object>> map) {
			return op.createMap(map);
		}

		public Object createNumeric(Number i) {
			return op.createNumeric(i);
		}

		public Object createString(String value) {
			return op.createString(value);
		}

		public Object empty() {
			return op.empty();
		}

		public Object emptyList() {
			return op.emptyList();
		}

		public <E> Function<E, DataResult<Object>> withEncoder(Encoder<E> encoder) {
			return op.withEncoder(encoder);
		}

		public Object emptyMap() {
			return op.emptyMap();
		}

		public DataResult<byte[]> getByteArray(Object input) {
			return op.getByteArray(input);
		}

		public <E> Function<Object, DataResult<Pair<E, Object>>> withDecoder(Decoder<E> decoder) {
			return op.withDecoder(decoder);
		}

		public <E> Function<Object, DataResult<E>> withParser(Decoder<E> decoder) {
			return op.withParser(decoder);
		}

		public DataResult<ByteBuffer> getByteBuffer(Object input) {
			return op.getByteBuffer(input);
		}

		public DataResult<int[]> getIntArray(Object input) {
			return op.getIntArray(input);
		}

		public DataResult<IntStream> getIntStream(Object input) {
			return op.getIntStream(input);
		}

		public DataResult<Consumer<Consumer<Object>>> getList(Object input) {
			return op.getList(input);
		}

		public DataResult<long[]> getLongArray(Object input) {
			return op.getLongArray(input);
		}

		public DataResult<LongStream> getLongStream(Object input) {
			return op.getLongStream(input);
		}

		public DataResult<MapLike<Object>> getMap(Object input) {
			return op.getMap(input);
		}

		public DataResult<Consumer<BiConsumer<Object, Object>>> getMapEntries(Object input) {
			return op.getMapEntries(input);
		}

		public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
			return op.getMapValues(input);
		}

		public DataResult<Number> getNumberValue(Object input) {
			return op.getNumberValue(input);
		}

		public Number getNumberValue(Object input, Number defaultValue) {
			return op.getNumberValue(input, defaultValue);
		}

		public DataResult<Stream<Object>> getStream(Object input) {
			return op.getStream(input);
		}

		public DataResult<String> getStringValue(Object input) {
			return op.getStringValue(input);
		}

		public ListBuilder<Object> listBuilder() {
			return op.listBuilder();
		}

		public RecordBuilder<Object> mapBuilder() {
			return op.mapBuilder();
		}

		public DataResult<Object> mergeToList(Object list, List<Object> values) {
			return op.mergeToList(list, values);
		}

		public DataResult<Object> mergeToList(Object list, Object value) {
			return op.mergeToList(list, value);
		}

		public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
			return op.mergeToMap(map, values);
		}

		public DataResult<Object> mergeToMap(Object map, MapLike<Object> values) {
			return op.mergeToMap(map, values);
		}

		public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
			return op.mergeToMap(map, key, value);
		}

		public DataResult<Object> mergeToPrimitive(Object prefix, Object value) {
			return op.mergeToPrimitive(prefix, value);
		}

		public Object remove(Object input, String key) {
			return op.remove(input, key);
		}

		public <U> U convertMap(DynamicOps<U> outOps, Object input) {
			return op.convertMap(outOps, input);
		}

		public DataResult<Object> get(Object input, String key) {
			return op.get(input, key);
		}

		public DataResult<Object> getGeneric(Object input, Object key) {
			return op.getGeneric(input, key);
		}

		public Object set(Object input, String key, Object value) {
			return op.set(input, key, value);
		}

		public Object update(Object input, String key, Function<Object, Object> function) {
			return op.update(input, key, function);
		}

		public Object updateGeneric(Object input, Object key, Function<Object, Object> function) {
			return op.updateGeneric(input, key, function);
		}

		public String toString() {
			return op.toString();
		}

		public Object createByteList(ByteBuffer input) {
			return op.createByteList(input);
		}

		public Object createIntList(IntStream input) {
			return op.createIntList(input);
		}

		public Object createLongList(LongStream input) {
			return op.createLongList(input);
		}

		public <U> U convertList(DynamicOps<U> outOps, Object input) {
			return op.convertList(outOps, input);
		}


		@Override
		public RegistryAccess registryAccess() {
			return registryAccess;
		}

		@Override
		public net.neoforged.neoforge.network.connection.ConnectionType connectionType() {
			return connectionType;
		}
	    
	}
	public DynamicOps<Object> toRegistryAccessable(RegistryFriendlyByteBuf buf) {
		return new RegisterFriendlyDataOps(this,buf);
	}
	public static final Object NULLTAG = new Object() {
		public String toString() {
			return "nulltag";
		}
	};
	boolean compress;

	public static Class<?> getElmClass(List<Object> objs) {
		if (!objs.isEmpty()) {
			if(objs.get(0)==null)return null;
			Class<?> cls = objs.get(0).getClass();
			for (Object obj : objs) {
				if (!cls.isInstance(obj))
					return null;
			}
			return cls;
		}
		return null;
	}

	public DataOps(boolean compress) {
		super();
		this.compress = compress;
	}

	@SuppressWarnings("unchecked")
	private static <T> DataResult<T> cast(Class<T> type, Object input) {
		if (type.isInstance(input))
			return DataResult.success((T) input);
		return DataResult.error(()->"Not a " + type.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	private static DataResult<Map<Object, Object>> castToMap(Object input) {
		if (input instanceof Map)
			return DataResult.success((Map<Object, Object>) input);
		return DataResult.error(()->"Not a Map");
	}

	@SuppressWarnings("unchecked")
	private static DataResult<List<Object>> castToList(Object input) {
		if (input instanceof List)
			return DataResult.success((List<Object>) input);
		return DataResult.error(()->"Not a List");
	}

	@Override
	public boolean compressMaps() {
		return compress;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U convertTo(DynamicOps<U> outOps, Object input) {
		if (input instanceof Byte) {
			return outOps.createByte((byte) input);
		} else if (input instanceof Short) {
			return outOps.createShort((short) input);
		} else if (input instanceof Integer) {
			return outOps.createInt((int) input);
		} else if (input instanceof Long) {
			return outOps.createLong((long) input);
		} else if (input instanceof Float) {
			return outOps.createFloat((float) input);
		} else if (input instanceof Double) {
			return outOps.createDouble((double) input);
		} else if (input instanceof String) {
			return outOps.createString((String) input);
		} else if (input instanceof Map) {
			return outOps.createMap(((Map<Object, Object>) input).entrySet().stream().map(o -> Pair.of(this.convertTo(outOps, o.getKey()), this.convertTo(outOps, o.getValue()))));
		} else if (input instanceof List) {
			List<Object> objs = ((List<Object>) input);
			Class<?> cls = getElmClass(objs);
			if (cls == Byte.class) {
				return outOps.createByteList(this.getByteBuffer(objs).result().get());
			} else if (cls == Integer.class) {
				return outOps.createIntList(this.getIntStream(objs).result().get());
			} else if (cls == Long.class) {
				return outOps.createLongList(this.getLongStream(objs).result().get());
			}
			return outOps.createList(objs.stream().map(o -> this.convertTo(outOps, o)));
		}
		return outOps.empty();
	}

	@Override
	public Object createList(Stream<Object> input) {
		// System.out.println("crlist");
		return input.collect(Collectors.toList());
	}

	@Override
	public Object createMap(Map<Object, Object> map) {
		return new HashMap<>(map);
	}

	@Override
	public Object createMap(Stream<Pair<Object, Object>> map) {
		return map.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
	}

	@Override
	public Object createNumeric(Number i) {
		return i;
	}

	@Override
	public Object createString(String value) {
		return value;
	}

	@Override
	public Object empty() {
		return NULLTAG;
	}

	@Override
	public Object emptyList() {
		return new ArrayList<>();
	}

	@Override
	public Object emptyMap() {
		return new HashMap<>();
	}

	public DataResult<byte[]> getByteArray(Object input) {
		DataResult<List<Object>> dr = castToList(input);
		if (dr.result().isPresent()) {
			List<Object> res = dr.result().get();
			if(res.isEmpty())
				return DataResult.success(new byte[0]);
			if (getElmClass(res) == Byte.class) {
				int siz = res.size();
				byte[] bs = new byte[siz];
				for (int i = 0; i < siz; i++)
					bs[i] = (Byte) res.get(i);
				return DataResult.success(bs);
			}
		}
		return DataResult.error(()->"Not a byte array");
	}

	@Override
	public DataResult<ByteBuffer> getByteBuffer(Object input) {
		return getByteArray(input).map(ByteBuffer::wrap);
	}

	public DataResult<int[]> getIntArray(Object input) {
		DataResult<List<Object>> dr = castToList(input);
		if (dr.result().isPresent()) {
			List<Object> res = dr.result().get();
			if(res.isEmpty())
				return DataResult.success(new int[0]);
			if (getElmClass(res) == Integer.class) {
				int siz = res.size();
				int[] bs = new int[siz];
				for (int i = 0; i < siz; i++)
					bs[i] = (Integer) res.get(i);
				return DataResult.success(bs);
			}

		}
		return DataResult.error(()->"Not a int array");
	}

	@Override
	public DataResult<IntStream> getIntStream(Object input) {
		return getIntArray(input).map(IntStream::of);
	}

	@Override
	public DataResult<Consumer<Consumer<Object>>> getList(Object input) {
		return castToList(input).map(t -> t::forEach);
	}

	public DataResult<long[]> getLongArray(Object input) {
		DataResult<List<Object>> dr = castToList(input);
		if (dr.result().isPresent()) {
			List<Object> res = dr.result().get();
			if(res.isEmpty())
				return DataResult.success(new long[0]);
			if (getElmClass(res) == Long.class) {
				int siz = res.size();
				long[] bs = new long[siz];
				for (int i = 0; i < siz; i++)
					bs[i] = (Long) res.get(i);
				return DataResult.success(bs);
			}

		}
		return DataResult.error(()->"Not a long array");
	}

	@Override
	public DataResult<LongStream> getLongStream(Object input) {
		return getLongArray(input).map(LongStream::of);
	}

	@Override
	public DataResult<MapLike<Object>> getMap(Object input) {
		return castToMap(input).map(o -> new MLike(o));
	}

	@Override
	public DataResult<Consumer<BiConsumer<Object, Object>>> getMapEntries(Object input) {
		return castToMap(input).map(s -> (c -> s.entrySet().forEach(p -> c.accept(p.getKey(), p.getValue()))));
	}

	@Override
	public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
		return castToMap(input).map(t -> t.entrySet().stream().map(o -> Pair.of(o.getKey(), o.getValue())));
	}

	@Override
	public DataResult<Number> getNumberValue(Object input) {
		return cast(Number.class, input);
	}

	@Override
	public Number getNumberValue(Object input, Number defaultValue) {
		return cast(Number.class, input).result().orElse(defaultValue);
	}

	@Override
	public DataResult<Stream<Object>> getStream(Object input) {
		return castToList(input).map(t -> t.stream());
	}

	@Override
	public DataResult<String> getStringValue(Object input) {
		return cast(String.class, input);
	}

	@Override
	public ListBuilder<Object> listBuilder() {
		return new LBuilder(this);
	}

	@Override
	public RecordBuilder<Object> mapBuilder() {
		return new MBuilder(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataResult<Object> mergeToList(Object list, List<Object> values) {
		if (list instanceof List) {
			List<Object> li = (List<Object>) list;
			li.addAll(values);
		} else if (list == NULLTAG || list == null) {
			return DataResult.success(values);
		}
		return DataResult.error(()->"Not a Map or Empty");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DataResult<Object> mergeToList(Object list, Object value) {
		// System.out.println(list);
		if (list == NULLTAG || list == null) {
			return DataResult.success(Stream.of(value).collect(Collectors.toList()));
		}
		;
		DataResult<List<Object>> ret = castToList(list);
		ret.result().ifPresent(t -> t.add(value));
		return (DataResult) ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
		if (map instanceof Map) {
			Map<Object, Object> li = (Map<Object, Object>) map;
			li.putAll(values);
		} else if (map == NULLTAG || map == null) {
			return DataResult.success(values);
		}
		return DataResult.error(()->"Not a Map or Empty");
	}

	@Override
	public DataResult<Object> mergeToMap(Object map, MapLike<Object> values) {
		if (values instanceof MLike) {
			return this.mergeToMap(map, ((MLike) values).map);
		}
		return DynamicOps.super.mergeToMap(map, values);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
		if (map == NULLTAG || map == null) {
			return DataResult.success(Stream.of(Pair.of(key, value)).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		}
		DataResult<Map<Object, Object>> ret = castToMap(map);
		ret.result().ifPresent(t -> t.put(key, value));
		return (DataResult) ret;
	}

	@Override
	public DataResult<Object> mergeToPrimitive(Object prefix, Object value) {
		if (prefix != NULLTAG && prefix != null) {
			return DataResult.error(()->"Do not know how to append a primitive value " + value + " to " + prefix, value);
		}
		return DataResult.success(value);
	}

	@Override
	public Object remove(Object input, String key) {
		return castToMap(input).result().map(t -> t.remove(key)).orElse(NULLTAG);
	}

	@Override
	public <U> U convertMap(DynamicOps<U> outOps, Object input) {
		return outOps.createMap(castToMap(input).result().orElse(ImmutableMap.of()).entrySet().stream().map(t -> Pair.of(convertTo(outOps, t.getKey()), convertTo(outOps, t.getValue()))));
	}

	@Override
	public DataResult<Object> get(Object input, String key) {
		return getGeneric(input, key);
	}

	@Override
	public DataResult<Object> getGeneric(Object input, Object key) {
		return castToMap(input).flatMap(t -> {
			Object value = t.get(key);
			return value == null ? DataResult.error(()->"No value for " + key + " present") : DataResult.success(value);
		});
	}

	@Override
	public Object set(Object input, String key, Object value) {
		return mergeToMap(input, key, value).result().orElse(input);
	}

	@Override
	public Object update(Object input, String key, Function<Object, Object> function) {
		DataResult<Map<Object,Object>> ret= castToMap(input);
		ret.result().ifPresent(t->t.compute(key, (k,v)->function.apply(v==null?NULLTAG:v)));
		return ret;
	}

	@Override
	public Object updateGeneric(Object input, Object key, Function<Object, Object> function) {
		DataResult<Map<Object,Object>> ret= castToMap(input);
		ret.result().ifPresent(t->t.compute(key, (k,v)->function.apply(v==null?NULLTAG:v)));
		return ret;
	}

	@Override
	public String toString() {
		return "Data" + (compress?"Compressed":"");
	}

	@Override
	public Object createByteList(ByteBuffer input) {
		List<Byte> list=new ArrayList<>();
		for(int i=0;i<input.capacity();i++)
			list.add(input.get(i));
		return list;
	}

	@Override
	public Object createIntList(IntStream input) {
		return input.boxed().collect(Collectors.toList());
	}

	@Override
	public Object createLongList(LongStream input) {
		return input.boxed().collect(Collectors.toList());
	}

	@Override
	public <U> U convertList(DynamicOps<U> outOps, Object input) {
		DataResult<List<Object>> result=castToList(input);
		if(!result.result().isPresent())
			return outOps.emptyList();
		List<Object> list=result.result().get();
		Class<?> clazz=getElmClass(list);
		if(clazz==Byte.class) {
			ByteBuffer bb=ByteBuffer.allocate(list.size());
			list.stream().forEach(b->bb.put((Byte)b));
			return outOps.createByteList(bb);
		}else if(clazz==Integer.class) {
			return outOps.createIntList(list.stream().mapToInt(t->(int)t));
		}else if(clazz==Long.class) {
			return outOps.createLongList(list.stream().mapToLong(t->(long)t));
		}
		return outOps.createList(list.stream().map(t->convertList(outOps, t)));
	}

}
