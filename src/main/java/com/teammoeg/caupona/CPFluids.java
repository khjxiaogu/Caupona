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

package com.teammoeg.caupona;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.generated.CPStewTexture;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class CPFluids {
	private static class TextureColorPair {
		ResourceLocation texture;
		int c;

		public TextureColorPair(ResourceLocation t, int c) {
			super();
			this.texture = t;
			this.c = c;
		}
		public FluidType create(String n){
			ResourceLocation rt=CPStewTexture.texture.getOrDefault(n, texture);
			int cx=CPStewTexture.texture.containsKey(n)?0xffffffff:c;
			FluidType ft=new FluidType(FluidType.Properties.create().viscosity(1200)
					.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item."+CPMain.MODID+"."+n)) {

						@Override
						public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
							consumer.accept(new IClientFluidTypeExtensions() {

								@Override
								public int getTintColor() {
									return cx;
								}

								@Override
								public ResourceLocation getStillTexture() {
									return rt;
								}

								@Override
								public ResourceLocation getFlowingTexture() {
									return rt;
								}
								
							});
						}
				
			};

			return ft;
		}
	}

	private static final ResourceLocation STILL_WATER_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
	private static final ResourceLocation STILL_SOUP_TEXTURE = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "block/soup_fluid");
	private static final ResourceLocation STILL_MILK_TEXTURE = ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still");
	static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, CPMain.MODID);
	static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, CPMain.MODID);
	//private static final Map<String, TextureColorPair> soupfluids = new HashMap<>();

	public static TextureColorPair soup(int c) {
		return new TextureColorPair(STILL_SOUP_TEXTURE, c);
	}

	public static TextureColorPair water(int c) {
		return new TextureColorPair(STILL_WATER_TEXTURE, c);
	}

	public static TextureColorPair milk(int c) {
		return new TextureColorPair(STILL_MILK_TEXTURE, c);
	}

	public static Stream<Fluid> getAll() {
		return Arrays.stream(CPItems.soups).map(e -> ResourceLocation.fromNamespaceAndPath(CPMain.MODID, e))
				.map(BuiltInRegistries.FLUID::get);
	}
	public static Stream<ResourceKey<Fluid>> getAllKeys() {
		return Arrays.stream(CPItems.soups).map(e -> ResourceLocation.fromNamespaceAndPath(CPMain.MODID, e))
				.map(e->ResourceKey.create(Registries.FLUID,e));
	}
	static {
		for (String i : CPItems.soups) {
			DeferredHolder<FluidType,FluidType> type=FLUID_TYPES.register(i,()->new TextureColorPair(CPStewTexture.texture.getOrDefault(i, STILL_SOUP_TEXTURE),0xffffffff).create(i));
			LazySupplier<Fluid> crf=new LazySupplier<>();
			crf.setVal(FLUIDS.register(i,
					() -> new SoupFluid(new BaseFlowingFluid.Properties(type, crf,
							crf).slopeFindDistance(1)
											.explosionResistance(100F))));
		}
	}
	public static class LazySupplier<T> implements Supplier<T>{
		Supplier<T> val;
		@Override
		public T get() {
			if(val==null)return null;
			return val.get();
		}
		public void setVal(Supplier<T> val) {
			this.val = val;
		}
		
		
	} 
	public static Set<String> getSoupfluids() {
		return ImmutableSet.copyOf(CPItems.soups);
	}
}
