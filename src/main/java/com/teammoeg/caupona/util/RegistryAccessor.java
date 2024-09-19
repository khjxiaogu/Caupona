package com.teammoeg.caupona.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class RegistryAccessor {
	public static class RegistryAccessorStack implements AutoCloseable{
		private ThreadRegistryAccess parent;
		private RegistryAccess accessor;
		private ConnectionType connectionType;
		public RegistryAccessorStack(ThreadRegistryAccess parent, RegistryAccess accessor, ConnectionType connectionType) {
			super();
			this.parent = parent;
			this.accessor = accessor;
			this.connectionType = connectionType;
		}
		public Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
			return RegistryFriendlyByteBuf.decorator(accessor, connectionType);
		}
		@Override
		public void close() {
			parent.pop();
		}
	}
	public static class ThreadRegistryAccess {
		LinkedList<RegistryAccessorStack> stack=new LinkedList<>();
		public ThreadRegistryAccess() {
			super();
		}
		public RegistryAccessorStack provideRegistryAccess(RegistryFriendlyByteBuf pb) {
			RegistryAccessorStack ras=new RegistryAccessorStack(this,pb.registryAccess(),pb.getConnectionType());
			stack.add(ras);
			return ras;
		}
		public Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
			if(!haveAccess())
				throw new NoSuchElementException("no registry access found");
			return stack.getLast().getDecorator();
		}
		public boolean haveAccess() {
			return !stack.isEmpty();
		}
		public void pop() {
			stack.pollLast();
		}

	}
	public static ThreadLocal<ThreadRegistryAccess> access=ThreadLocal.withInitial(ThreadRegistryAccess::new);
	public static boolean haveAccess() {
		return access.get().haveAccess();
	}
	public static Function<ByteBuf, RegistryFriendlyByteBuf> getDecorator() {
		return access.get().getDecorator();
	}
	
	public static RegistryAccessorStack provideRegistryAccess(RegistryFriendlyByteBuf pb) {
		return access.get().provideRegistryAccess(pb);
	}
}
