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
