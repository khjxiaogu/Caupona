package com.teammoeg.caupona.util;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;
import oshi.driver.windows.wmi.Win32BaseBoard.BaseBoardProperty;

public class RegistryAccessor {
	public static class CloseableRegistryAccessor implements AutoCloseable{
		@Override
		public void close() {
			RegistryAccessor.close();
		}
	}
	private static RegistryAccess accessor;
	private static ConnectionType connectionType;
	private static CloseableRegistryAccessor closer=new CloseableRegistryAccessor();
	public static boolean haveAccess() {
		return accessor!=null;
	}
	public static RegistryAccess getRegistryAccess() {

		return accessor;
	}
	public static Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {

		return RegistryFriendlyByteBuf.decorator(accessor, connectionType);
	}
	
	public static void provideRegistryAccess(RegistryFriendlyByteBuf pb) {
		accessor=pb.registryAccess();
		connectionType=pb.getConnectionType();
	}
	public static void close() {
		accessor=null;
		connectionType=null;
	}
	public static CloseableRegistryAccessor automated(RegistryFriendlyByteBuf pb) {
		provideRegistryAccess(pb);
		return closer;
	}
}
