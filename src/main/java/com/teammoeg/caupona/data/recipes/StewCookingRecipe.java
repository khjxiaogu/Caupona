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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPTags;
import com.teammoeg.caupona.CPTags.Items;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.recipes.baseconditions.BaseConditions;
import com.teammoeg.caupona.data.recipes.conditions.Conditions;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class StewCookingRecipe extends IDataRecipe implements IConditionalRecipe {
	public StewCookingRecipe(List<IngredientCondition> allow, List<IngredientCondition> deny, int priority, int time, float density, List<StewBaseCondition> base, Fluid output, boolean removeNBT) {
		super();
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = base;
		this.output = output;
		this.removeNBT = removeNBT;
	}

	public static Set<CookIngredients> cookables;
	public static Set<Fluid> allOutput;
	public static List<RecipeHolder<StewCookingRecipe>> sorted;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;

	public static boolean isCookable(ItemStack stack) {
		FloatemTagStack s = new FloatemTagStack(stack);
		return stack.is(Items.COOKABLE) || cookables.stream().anyMatch(e -> e.fits(s));
		// return true;
	}

	@SuppressWarnings("deprecation")
	public static boolean isBoilable(FluidStack f) {
		Fluid fd = f.getFluid();
		return fd instanceof SoupFluid || f.getFluid().is(CPTags.Fluids.BOILABLE)||BoilingRecipe.allBoilables.contains(f.getFluid());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	List<IngredientCondition> allow;
	List<IngredientCondition> deny;
	int priority = 0;
	public int time;
	float density;
	List<StewBaseCondition> base;
	public Fluid output;
	public boolean removeNBT=false;
	public static final MapCodec<StewCookingRecipe> CODEC=
		RecordCodecBuilder.mapCodec(t->t.group(
			Codec.list(Conditions.CODEC).optionalFieldOf("allow").forGetter(o->Optional.ofNullable(o.allow)),
			Codec.list(Conditions.CODEC).optionalFieldOf("deny").forGetter(o->Optional.ofNullable(o.deny)),
			Codec.INT.fieldOf("priority").forGetter(o->o.priority),
			Codec.INT.fieldOf("time").forGetter(o->o.time),
			Codec.FLOAT.fieldOf("density").forGetter(o->o.density),
			Codec.list(BaseConditions.CODEC).optionalFieldOf("base").forGetter(o->Optional.ofNullable(o.base)),
			BuiltInRegistries.FLUID.byNameCodec().fieldOf("output").forGetter(o->o.output),
			Codec.BOOL.fieldOf("removeNBT").forGetter(o->o.removeNBT)
				).apply(t, StewCookingRecipe::new));
//	public StewCookingRecipe(FriendlyByteBuf data) {
//		allow = SerializeUtil.readList(data, Conditions::of);
//		deny = SerializeUtil.readList(data, Conditions::of);
//		priority = data.readVarInt();
//		density = data.readFloat();
//		time = data.readVarInt();
//		base = SerializeUtil.readList(data, BaseConditions::of);
//		output = data.readById(BuiltInRegistries.FLUID::byId);
//		removeNBT=data.readBoolean();
//	}

	public StewCookingRecipe(Optional<List<IngredientCondition>> allow, Optional<List<IngredientCondition>> deny,
			int priority, int time, float density, Optional<List<StewBaseCondition>> base, Fluid output,boolean removeNBT) {
		this.allow = allow.orElseGet(ImmutableList::of);
		this.deny = deny.orElseGet(ImmutableList::of);
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = base.orElseGet(ImmutableList::of);
		this.output = output;
		this.removeNBT=removeNBT;
	}
/*
	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, allow, Conditions::write);
		SerializeUtil.writeList(data, deny, Conditions::write);
		data.writeVarInt(priority);
		data.writeFloat(density);
		data.writeVarInt(time);
		SerializeUtil.writeList(data, base, BaseConditions::write);
		data.writeById(BuiltInRegistries.FLUID::getId,output);
		data.writeBoolean(removeNBT);
	}*/

	public int matches(StewPendingContext ctx) {
		if (ctx.getTotalItems() < density)
			return 0;
		int matchtype = 0;
		if (base != null&&!base.isEmpty()) {
			for (StewBaseCondition e : base) {
				matchtype = ctx.compute(e);
				if (matchtype != 0)
					break;
			}
			if (matchtype == 0)
				return 0;
		}
		if (matchtype == 0)
			matchtype = 1;
		if (!allow.isEmpty())
			if (!allow.stream().allMatch(ctx::compute))
				return 0;
		if (!deny.isEmpty())
			if (deny.stream().anyMatch(ctx::compute))
				return 0;
		return matchtype;
	}

	public Stream<CookIngredients> getAllNumbers() {
		return Stream.concat(
				allow.stream().flatMap(IngredientCondition::getAllNumbers),
				deny.stream().flatMap(IngredientCondition::getAllNumbers));
	}

	public Stream<ResourceLocation> getTags() {
		return Stream.concat(allow.stream().flatMap(IngredientCondition::getTags),
				deny.stream().flatMap(IngredientCondition::getTags));
	}

	public int getPriority() {
		return priority;
	}

	public List<StewBaseCondition> getBase() {
		return base;
	}

	@Override
	public List<IngredientCondition> getAllow() {
		return allow;
	}

	@Override
	public List<IngredientCondition> getDeny() {
		return deny;
	}

	public float getDensity() {
		return density;
	}

}
