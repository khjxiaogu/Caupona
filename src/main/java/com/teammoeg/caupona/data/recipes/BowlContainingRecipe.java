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

package com.teammoeg.caupona.data.recipes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BowlContainingRecipe extends IDataRecipe {
	public static Map<Ingredient,List<RecipeHolder<BowlContainingRecipe>>> recipes;
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
	public static List<RecipeHolder<BowlContainingRecipe>> getRecipes(ItemStack bowl){
		for(Entry<Ingredient, List<RecipeHolder<BowlContainingRecipe>>> i:recipes.entrySet()) {
			if(i.getKey().test(bowl)) {
				return i.getValue();
			}
		}
		return ImmutableList.of();
	}
	public static boolean isBowl(ItemStack bowl){
		for(Ingredient i:recipes.keySet()) {
			if(i.test(bowl)) {
				return true;
			}
		}
		return false;
	}
	/*public static List<RecipeHolder<BowlContainingRecipe>> getWoodenRecipes(){
		return recipes.get(WOODEN_BOWL);
	}
	public static List<RecipeHolder<BowlContainingRecipe>> getBreadRecipes(){
		return recipes.get(BREAD_BOWL);
	}*/
	public Item bowl;
	public Ingredient inBowl;
	public FluidIngredient fluid;
	public static final MapCodec<BowlContainingRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(o->o.bowl),
					FluidIngredient.CODEC.fieldOf("fluid").forGetter(o->o.fluid),
					Ingredient.CODEC.fieldOf("inType").forGetter(o->o.inBowl)
					).apply(t, BowlContainingRecipe::new));
/*
	public BowlContainingRecipe(FriendlyByteBuf pb) {
		bowl = pb.readById(BuiltInRegistries.ITEM);
		fluid = pb.readById(BuiltInRegistries.FLUID);
	}*/

	public BowlContainingRecipe(Item bowl, Fluid fluid,Ingredient bowlType) {
		this.bowl = bowl;
		this.fluid = FluidIngredient.of(fluid);
		this.inBowl=bowlType;
	}
	public BowlContainingRecipe(Item bowl, FluidIngredient fluid,Ingredient bowlType) {
		this.bowl = bowl;
		this.fluid = fluid;
		this.inBowl=bowlType;
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

	public boolean matches(FluidStack f) {
		return fluid.test(f);
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(bowl);
		
		is.applyComponents(stack.getComponents());
		is.set(CPCapability.ITEM_FLUID, new ItemHoldedFluidData(stack.getFluid()));
		return is;
	}

	public Object test(Fluid fluid2) {
		return fluid.test(new FluidStack(fluid2,250));
	}

}
