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

package com.teammoeg.caupona.data.recipes;

import java.util.Map;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BoilingRecipe extends IDataRecipe {
	public static Map<Fluid, RecipeHolder<BoilingRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static Set<Fluid> allBoilables;
	public Fluid before;
	public Fluid after;
	public int time;
	public static final MapCodec<BoilingRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					BuiltInRegistries.FLUID.byNameCodec().fieldOf("from").forGetter(o->o.before),
					BuiltInRegistries.FLUID.byNameCodec().fieldOf("to").forGetter(o->o.after),
					Codec.INT.fieldOf("time").forGetter(o->o.time)).apply(t, BoilingRecipe::new));
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}
/*

	public BoilingRecipe(FriendlyByteBuf data) {
		before = data.readById(BuiltInRegistries.FLUID);
		after = data.readById(BuiltInRegistries.FLUID);
		time = data.readVarInt();
	}
*/
	public BoilingRecipe(Fluid before, Fluid after, int time) {
		this.before = before;
		this.after = after;
		this.time = time;
	}
/*
	public void write(FriendlyByteBuf data) {
		data.writeId(BuiltInRegistries.FLUID,before);
		data.writeId(BuiltInRegistries.FLUID,after);
		data.writeVarInt(time);
	}
*/
	public FluidStack handle(FluidStack org) {
		FluidStack fs = new FluidStack(after, org.getAmount());
		fs.applyComponents(org.getComponentsPatch());
		return fs;
	}
}
