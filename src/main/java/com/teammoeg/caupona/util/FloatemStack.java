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

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class FloatemStack {
	ItemStack stack;
	float count;
	public static final Codec<FloatemStack> CODEC=RecordCodecBuilder.create(o->o.group(ItemStack.CODEC.fieldOf("item").forGetter(i->i.stack)
		,Codec.FLOAT.fieldOf("count").forGetter(i->i.count))
		.apply(o, FloatemStack::new)
		
		);
	public FloatemStack(ItemStack stack, float count) {
		super();
		this.stack = stack.copy();
		this.stack.setCount(1);
		this.count = count;
	}

	public FloatemStack(CompoundTag nbt,HolderLookup.Provider registry) {
		super();
		this.deserializeNBT(nbt,registry);
	}

	public FloatemStack(ItemStack is) {
		this(is, is.getCount());
	}

	public ItemStack getStack() {
		return stack.copy();
	}

	public ItemStack getCraftingRemainingItem() {
		return stack.getCraftingRemainingItem();
	}

	public boolean hasContainerItem() {
		return stack.hasCraftingRemainingItem();
	}

	public CompoundTag serializeNBT(HolderLookup.Provider registry) {
		CompoundTag cnbt = (CompoundTag) stack.save(registry);
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}

	public boolean isEmpty() {
		return count <= 0.001;
	}

	public Item getItem() {
		return stack.getItem();
	}

	public int getEntityLifespan(Level world) {
		return stack.getEntityLifespan(world);
	}

	public CompoundTag write(CompoundTag nbt,HolderLookup.Provider registry) {
		CompoundTag cnbt = (CompoundTag) stack.save(registry);
		for(String key:cnbt.getAllKeys()) {
			nbt.put(key, cnbt.get(key));
		}
		nbt.putFloat("th_countf", count);
		
		return cnbt;
	}

	public int getMaxStackSize() {
		return stack.getMaxStackSize();
	}

	public boolean isStackable() {
		return stack.isStackable();
	}

	public boolean isDamageable() {
		return stack.isDamageableItem();
	}

	public boolean isDamaged() {
		return stack.isDamaged();
	}

	public int getDamage() {
		return stack.getDamageValue();
	}

	public void setDamage(int damage) {
		stack.setDamageValue(damage);
	}

	public int getMaxDamage() {
		return stack.getMaxDamage();
	}


	public FloatemStack copy() {
		return new FloatemStack(stack.copy(), this.count);
	}

	public boolean isItemEqual(ItemStack other) {
		return ItemStack.isSameItem(stack,other);
	}


	public String getTranslationKey() {
		return stack.getDescriptionId();
	}

	public boolean hasTag() {
		return stack.has(DataComponents.CUSTOM_DATA);
	}

	public CompoundTag getTagForRead() {
		return stack.get(DataComponents.CUSTOM_DATA).copyTag();
	}

	public Stream<ResourceLocation> getTags() {
		return stack.getTags().map(TagKey::location);
	}

	public void updateTag(Consumer<CompoundTag> update) {
		CustomData data=stack.get(DataComponents.CUSTOM_DATA);
		CompoundTag tag;
		if(data!=null) {
			tag=data.copyTag();
		}else {
			tag=new CompoundTag();
		}
		update.accept(tag);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}


	public void setTag(CompoundTag nbt) {
		CustomData.set(DataComponents.CUSTOM_DATA, stack, nbt);
	}

	public Component getDisplayName() {
		return stack.getHoverName();
	}

	public boolean hasEffect() {
		return stack.hasFoil();
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

	public void deserializeNBT(CompoundTag nbt,HolderLookup.Provider registry) {
		stack = ItemStack.parse(registry, nbt).orElse(ItemStack.EMPTY);
		this.count = nbt.getFloat("th_countf");
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
}
