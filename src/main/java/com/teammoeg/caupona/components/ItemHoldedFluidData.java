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

package com.teammoeg.caupona.components;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public record ItemHoldedFluidData(Fluid fluidType) {
	public static final Codec<ItemHoldedFluidData> CODEC=RecordCodecBuilder.create(t->t.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluidType))
		.apply(t, ItemHoldedFluidData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemHoldedFluidData> STREAM_CODEC=
		ByteBufCodecs.registry(BuiltInRegistries.FLUID.key()).map(ItemHoldedFluidData::new, ItemHoldedFluidData::getFluidType);
		;


	public Fluid getFluidType() {
		return fluidType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fluidType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ItemHoldedFluidData other = (ItemHoldedFluidData) obj;
		return Objects.equals(fluidType, other.fluidType);
	}


}
