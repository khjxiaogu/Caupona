package com.teammoeg.caupona.util;

import java.util.NoSuchElementException;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class RegistryAccessor {
	public static class CloseableRegistryAccessor implements AutoCloseable{
		ThreadRegistryAccess access;
		public CloseableRegistryAccessor(ThreadRegistryAccess access) {
			super();
			this.access = access;
		}
		@Override
		public void close() {
			access.close();
		}
	}
	public static class ThreadRegistryAccess {
		private transient RegistryAccess accessor;
		private transient ConnectionType connectionType;
		private transient CloseableRegistryAccessor closer=new CloseableRegistryAccessor(this);
		public ThreadRegistryAccess() {
			super();
		}
		public void provideRegistryAccess(RegistryFriendlyByteBuf pb) {
			accessor=pb.registryAccess();
			connectionType=pb.getConnectionType();
		}
		public void close() {
			accessor=null;
			connectionType=null;
		}
		public Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
			if(!haveAccess())
				throw new NoSuchElementException("no registry access found");
			return RegistryFriendlyByteBuf.decorator(accessor, connectionType);
		}
		public boolean haveAccess() {
			return accessor!=null;
		}
		public RegistryAccess getRegistryAccess() {
			return accessor;
		}
		public CloseableRegistryAccessor automated(RegistryFriendlyByteBuf pb) {
			provideRegistryAccess(pb);
			return closer;
		}
	}
	public static ThreadLocal<ThreadRegistryAccess> access=ThreadLocal.withInitial(ThreadRegistryAccess::new);
	public static boolean haveAccess() {
		return access.get().haveAccess();
	}
	public static RegistryAccess getRegistryAccess() {
		return access.get().getRegistryAccess();
	}
	public static Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
		return access.get().getDecorator();
	}
	
	public static void provideRegistryAccess(RegistryFriendlyByteBuf pb) {
		access.get().provideRegistryAccess(pb);
	}
	public static void close() {
		access.get().close();
	}
	public static CloseableRegistryAccessor automated(RegistryFriendlyByteBuf pb) {
		return access.get().automated(pb);
	}
}
