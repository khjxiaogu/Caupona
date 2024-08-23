package com.teammoeg.caupona.util;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IRegistryAccessable {

	RegistryAccess registryAccess();

	net.neoforged.neoforge.network.connection.ConnectionType connectionType();
	default Function<ByteBuf, RegistryFriendlyByteBuf> decorator() {
		return RegistryFriendlyByteBuf.decorator(registryAccess(), connectionType());
	}
}