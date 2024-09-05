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
