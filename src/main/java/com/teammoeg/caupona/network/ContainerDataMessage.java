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

package com.teammoeg.caupona.network;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.ClientProxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ContainerDataMessage implements CustomPacketPayload{
	private CompoundTag nbt;
	public static final Type<ContainerDataMessage> type=new Type<>(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"container_data"));
	public static final StreamCodec<ByteBuf, ContainerDataMessage> CODEC=ByteBufCodecs.COMPOUND_TAG.map(ContainerDataMessage::new, ContainerDataMessage::getTag);
	public ContainerDataMessage(CompoundTag message) {
		this.nbt = message;
	}

	ContainerDataMessage(FriendlyByteBuf buffer) {
		nbt = buffer.readNbt();
	}

	public CompoundTag getTag() {
		return nbt;
	}

	void handle(IPayloadContext context) {
		context.enqueueWork(()->ClientProxy.syncContainerInfo(nbt));
	}

	@Override
	public Type<ContainerDataMessage> type() {
		return type;
	}

}
