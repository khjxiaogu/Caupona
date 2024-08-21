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

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTag;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidType;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTypeType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class StewRecipeBuilder {
	public static class StewBaseBuilder {
		private StewRecipeBuilder parent;

		public StewBaseBuilder(StewRecipeBuilder parent) {
			super();
			this.parent = parent;
		}

		public StewBaseBuilder tag(ResourceLocation rl) {
			parent.base.add(new FluidTag(rl));
			return this;
		}
		public StewBaseBuilder tag(TagKey<Fluid> rl) {
			parent.base.add(new FluidTag(rl.location()));
			return this;
		}

		public StewBaseBuilder type(Fluid f) {
			parent.base.add(new FluidType(f));
			return this;
		}

		public StewBaseBuilder only(Fluid f) {
			parent.base.add(new FluidTypeType(f));
			return this;
		}

		public StewRecipeBuilder and() {
			return parent;
		}
	}

	private List<IngredientCondition> allow = new ArrayList<>();
	private List<IngredientCondition> deny = new ArrayList<>();
	private int priority = 0;
	private int time = 200;
	private float density = 0.75f;
	private List<StewBaseCondition> base = new ArrayList<>();
	private Fluid output;
	private ResourceLocation id;
	private boolean removeNBT=false;
	public StewRecipeBuilder(ResourceLocation id, Fluid out) {
		output = out;
		this.id = id;
	}

	public static StewRecipeBuilder start(Fluid out) {
		return new StewRecipeBuilder(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "cooking/" + Utils.getRegistryName(out).getPath()),
				out);
	}

	public IngredientConditionsBuilder<StewRecipeBuilder> require() {
		return new IngredientConditionsBuilder<StewRecipeBuilder>(this, allow, allow, deny);
	}

	public IngredientConditionsBuilder<StewRecipeBuilder> not() {
		return new IngredientConditionsBuilder<StewRecipeBuilder>(this, deny, allow, deny);
	}

	public StewBaseBuilder base() {
		return new StewBaseBuilder(this);
	}

	public StewRecipeBuilder prio(int p) {
		priority = p;
		return this;
	}

	public StewRecipeBuilder special() {
		priority |= 1024;
		return this;
	}

	public StewRecipeBuilder high() {
		priority |= 128;
		return this;
	}

	public StewRecipeBuilder med() {
		priority |= 64;
		return this;
	}

	public StewRecipeBuilder low() {
		return this;
	}

	public StewRecipeBuilder time(int t) {
		time = t;
		return this;
	}

	public StewRecipeBuilder dense(double d) {
		density = (float) d;
		return this;
	}
	public StewRecipeBuilder removeNBT() {
		removeNBT=true;
		return this;
	}
	public StewCookingRecipe end() {
		return new StewCookingRecipe( allow, deny, priority, time, density, base, output,removeNBT);
	}

	public StewCookingRecipe finish(BiConsumer<ResourceLocation, IDataRecipe> out) {
		StewCookingRecipe r = end();
		out.accept(id,r);
		return r;
	}
}
