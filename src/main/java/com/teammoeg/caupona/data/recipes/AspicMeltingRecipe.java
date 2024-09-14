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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AspicMeltingRecipe extends IDataRecipe {
	public static List<RecipeHolder<AspicMeltingRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static final MapCodec<AspicMeltingRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					Ingredient.CODEC.fieldOf("aspic").forGetter(o->o.aspic),
					BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluid),
					Codec.INT.fieldOf("amount").forGetter(o->o.amount),
					Codec.INT.fieldOf("time").forGetter(o->o.time)).apply(t, AspicMeltingRecipe::new));
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public Ingredient aspic;
	public Fluid fluid;
	public int amount = 250;
	public int time = 100;
/*
	public AspicMeltingRecipe(FriendlyByteBuf pb) {

		aspic = Ingredient.fromNetwork(pb);
		fluid = pb.readById(BuiltInRegistries.FLUID);
		amount = pb.readVarInt();
		time = pb.readVarInt();
	}*/

	public AspicMeltingRecipe(Ingredient aspic, Fluid fluid) {
		this.aspic = aspic;
		this.fluid = fluid;
	}

	public AspicMeltingRecipe(Ingredient aspic, Fluid fluid, int amount, int time) {
		super();
		this.aspic = aspic;
		this.fluid = fluid;
		this.amount = amount;
		this.time = time;
	}

	/*public void write(FriendlyByteBuf pack) {
		aspic.toNetwork(pack);
		pack.writeId(BuiltInRegistries.FLUID, fluid);
		pack.writeVarInt(amount);
		pack.writeVarInt(time);
	}
*/
	public FluidStack handle(ItemStack s) {
		StewInfo si=s.get(CPCapability.STEW_INFO);
		FluidStack fs = new FluidStack(fluid, amount);
		fs.set(CPCapability.STEW_INFO, si.toImmutable());
		return fs;
	}

	public StewInfo info(ItemStack s) {
		StewInfo si = s.get(CPCapability.STEW_INFO);
		return si;
	}

	public static AspicMeltingRecipe find(ItemStack aspic) {
		return recipes.stream().map(t->t.value()).filter(t -> t.aspic.test(aspic)).findFirst().orElse(null);

	}
}
