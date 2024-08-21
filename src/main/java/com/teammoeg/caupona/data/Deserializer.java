package com.teammoeg.caupona.data;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Deserializer<U> {
	private int id;
	public MapCodec<U> fromJson;
	public StreamCodec<ByteBuf, U> fromPacket;

	public Deserializer(MapCodec<U> fromJson, StreamCodec<FriendlyByteBuf, U> fromPacket,int id) {
		super();
		this.fromJson = fromJson;
		this.fromPacket = fromPacket.mapStream(FriendlyByteBuf::new);
		this.id=id;
	}

	public U read(FriendlyByteBuf packet) {
		return fromPacket.decode(packet);
	}

	public int getId() {
		return id;
	}

}