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