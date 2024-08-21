package com.teammoeg.caupona.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public class ItemHoldedFluidData {
	public static final Codec<ItemHoldedFluidData> CODEC=RecordCodecBuilder.create(t->t.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluidType))
		.apply(t, ItemHoldedFluidData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemHoldedFluidData> STREAM_CODEC=
		ByteBufCodecs.registry(BuiltInRegistries.FLUID.key()).map(ItemHoldedFluidData::new, ItemHoldedFluidData::getFluidType);
		;
	Fluid fluidType;

	public ItemHoldedFluidData(Fluid fluidType) {
		super();
		this.fluidType = fluidType;
	}

	public Fluid getFluidType() {
		return fluidType;
	}


}
