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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BowlContainingRecipe extends IDataRecipe {
	public static Map<Fluid, RecipeHolder<BowlContainingRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
 
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public Item bowl;
	public Fluid fluid;
	public static final MapCodec<BowlContainingRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(o->o.bowl),
					BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluid)
					).apply(t, BowlContainingRecipe::new));
/*
	public BowlContainingRecipe(FriendlyByteBuf pb) {
		bowl = pb.readById(BuiltInRegistries.ITEM);
		fluid = pb.readById(BuiltInRegistries.FLUID);
	}*/

	public BowlContainingRecipe(Item bowl, Fluid fluid) {
		this.bowl = bowl;
		this.fluid = fluid;
	}/*

	public void write(FriendlyByteBuf pack) {
		pack.writeId(BuiltInRegistries.ITEM, bowl);
		pack.writeId(BuiltInRegistries.FLUID, fluid);
	}*/


	public ItemStack handle(Fluid f) {
		ItemStack is = new ItemStack(bowl);
		Utils.writeItemFluid(is, f);
		return is;
	}

	public boolean matches(Fluid f) {
		return fluid == f;
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(bowl);
		
		is.applyComponents(stack.getComponents());
		is.set(CPCapability.ITEM_FLUID, new ItemHoldedFluidData(stack.getFluid()));
		return is;
	}
}
