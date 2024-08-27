package com.teammoeg.caupona.data;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Deserializer<U> {
	private int id;
	public MapCodec<U> fromJson;
	public StreamCodec<RegistryFriendlyByteBuf, U> fromPacket;

	public Deserializer(MapCodec<U> fromJson, StreamCodec<RegistryFriendlyByteBuf, U> fromPacket,int id) {
		super();
		this.fromJson = fromJson;
		this.fromPacket = fromPacket;
		this.id=id;
	}

	public U read(RegistryFriendlyByteBuf packet) {
		return fromPacket.decode(packet);
	}

	public int getId() {
		return id;
	}

}