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

package com.teammoeg.caupona.util;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class FloatemStack {
	private final ItemStack stack;
	public float count;
	public static final Codec<FloatemStack> CODEC=RecordCodecBuilder.create(o->o.group(SerializeUtil.fromRFBBStreamCodec(ItemStack.STREAM_CODEC,ItemStack.CODEC).fieldOf("item").forGetter(i->i.stack)
		,Codec.FLOAT.fieldOf("count").forGetter(i->i.count))
		.apply(o, FloatemStack::new)
		
		);
	public FloatemStack(ItemStack stack, float count) {
		super();
		this.stack = stack.copy();
		this.stack.setCount(1);
		this.count = count;
	}

	public FloatemStack(ItemStack is) {
		this(is, is.getCount());
	}

	public ItemStack getStack() {
		return stack.copy();
	}
/*
	public CompoundTag serializeNBT(HolderLookup.Provider registry) {
		CompoundTag cnbt = (CompoundTag) stack.save(registry);
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}*/

	public boolean isEmpty() {
		return count <= 0.001;
	}

	public Item getItem() {
		return stack.getItem();
	}
/*
	public CompoundTag write(CompoundTag nbt,HolderLookup.Provider registry) {
		CompoundTag cnbt = (CompoundTag) stack.save(registry);
		for(String key:cnbt.getAllKeys()) {
			nbt.put(key, cnbt.get(key));
		}
		nbt.putFloat("th_countf", count);
		
		return cnbt;
	}*/

	public int getMaxStackSize() {
		return stack.getMaxStackSize();
	}

	public boolean isStackable() {
		return stack.isStackable();
	}

	public boolean isDamageable() {
		return stack.isDamageableItem();
	}

	public FloatemStack copy() {
		return new FloatemStack(stack.copy(), this.count);
	}
	public FloatemStack copyWithCount(float count) {
		return new FloatemStack(stack.copy(), count);
	}

	public boolean isItemEqual(ItemStack other) {
		return ItemStack.isSameItem(stack,other);
	}


	public String getTranslationKey() {
		return stack.getDescriptionId();
	}


	public Stream<ResourceLocation> getTags() {
		return stack.getTags().map(TagKey::location);
	}
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	public Rarity getRarity() {
		return stack.getRarity();
	}


	public Component getTextComponent() {
		return stack.getDisplayName();
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public void grow(float count) {
		this.count += count;
	}

	public void shrink(float count) {
		this.count -= count;
		if (this.count < 0)
			this.count = 0;
	}


	public boolean equals(ItemStack other) {
		return ItemStack.isSameItemSameComponents(this.getStack(), other);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(count);
		result = prime * result + ((stack == null) ? 0 : stack.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatemStack other = (FloatemStack) obj;
		if (Float.floatToIntBits(count) != Float.floatToIntBits(other.count))
			return false;
		if (stack == null) {
			if (other.stack != null)
				return false;
		} else if (!equals(other.stack))
			return false;
		return true;
	}

	public <T> T get(DataComponentType<? extends T> component) {
		return stack.get(component);
	}

	public <T> @Nullable T get(Supplier<? extends DataComponentType<? extends T>> type) {
		return stack.get(type);
	}

	public <T> @Nullable T set(Supplier<? extends DataComponentType<? super T>> componentType, @Nullable T value) {
		return stack.set(componentType, value);
	}

	public <T, U> @Nullable T update(Supplier<? extends DataComponentType<T>> componentType, T value, U updateContext, BiFunction<T, U, T> updater) {
		return stack.update(componentType, value, updateContext, updater);
	}

	public <T> @Nullable T update(Supplier<? extends DataComponentType<T>> componentType, T value, UnaryOperator<T> updater) {
		return stack.update(componentType, value, updater);
	}

	public void copyFrom(DataComponentHolder src, DataComponentType<?>... componentTypes) {
		stack.copyFrom(src, componentTypes);
	}

	public void copyFrom(DataComponentHolder src, Supplier<? extends DataComponentType<?>>... componentTypes) {
		stack.copyFrom(src, componentTypes);
	}

	public <T> T set(DataComponentType<? super T> component, T value) {
		return stack.set(component, value);
	}

	public <T, U> T update(DataComponentType<T> component, T defaultValue, U updateValue, BiFunction<T, U, T> updater) {
		return stack.update(component, defaultValue, updateValue, updater);
	}

	public <T> T update(DataComponentType<T> component, T defaultValue, UnaryOperator<T> updater) {
		return stack.update(component, defaultValue, updater);
	}
}
