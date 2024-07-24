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

import java.util.Objects;
import java.util.Optional;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

public class SpicedFoodInfo{
	public MobEffectInstance spice;
	public boolean hasSpice = false;
	public ResourceLocation spiceName;
	public SpicedFoodInfo() {}
	public SpicedFoodInfo(Optional<MobEffectInstance> spice, Boolean hasSpice, Optional<ResourceLocation> spiceName) {
		this.spice=spice.orElse(null);
		this.hasSpice=hasSpice;
		this.spiceName=spiceName.orElse(null);
	}
	
	public static <P extends SpicedFoodInfo> P3<Mu<P>, Optional<MobEffectInstance>, Boolean, Optional<ResourceLocation>>  codecStart(Instance<P> i) {
		return i.group(MobEffectInstance.CODEC.optionalFieldOf("spice").forGetter(o->Optional.ofNullable(o.spice)), Codec.BOOL.fieldOf("hasSpice").forGetter(o->o.hasSpice), ResourceLocation.CODEC.optionalFieldOf("spiceName").forGetter(o->Optional.ofNullable(o.spiceName)));
		
	}

	public boolean addSpice(MobEffectInstance spice, ItemStack im) {
		if (this.spice != null)
			return false;
		this.spice = new MobEffectInstance(spice);
		hasSpice = true;
		this.spiceName =Utils.getRegistryName(im);
		return true;
	}

	public void clearSpice() {
		spice = null;
		hasSpice = false;
		spiceName = null;
	}

	public boolean canAddSpice() {
		return !hasSpice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hasSpice, spice, spiceName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SpicedFoodInfo other = (SpicedFoodInfo) obj;
		return hasSpice == other.hasSpice && Objects.equals(spice, other.spice) && Objects.equals(spiceName, other.spiceName);
	}
}
