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
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class SauteedRecipeBuilder {

	private List<IngredientCondition> allow = new ArrayList<>();
	private List<IngredientCondition> deny = new ArrayList<>();
	private int priority = 0;
	private int time = 200;
	private Item output;
	private ResourceLocation id;
	private boolean removeNBT=false;
	private float per=2;
	private ResourceLocation model;
	public SauteedRecipeBuilder(ResourceLocation id, Item out,ResourceLocation model) {
		output = out;
		this.id = id;
		this.model=model;
	}

	public static SauteedRecipeBuilder start(ResourceLocation model, Item out) {
		return new SauteedRecipeBuilder(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "frying/" + Utils.getRegistryName(out).getPath()),
				out,model);
	}

	public IngredientConditionsBuilder<SauteedRecipeBuilder> require() {
		return new IngredientConditionsBuilder<SauteedRecipeBuilder>(this, allow, allow, deny);
	}

	public IngredientConditionsBuilder<SauteedRecipeBuilder> not() {
		return new IngredientConditionsBuilder<SauteedRecipeBuilder>(this, deny, allow, deny);
	}

	public SauteedRecipeBuilder prio(int p) {
		priority = p;
		return this;
	}

	public SauteedRecipeBuilder special() {
		priority |= 1024;
		return this;
	}

	public SauteedRecipeBuilder high() {
		priority |= 128;
		return this;
	}

	public SauteedRecipeBuilder med() {
		priority |= 64;
		return this;
	}

	public SauteedRecipeBuilder low() {
		return this;
	}

	public SauteedRecipeBuilder time(int t) {
		time = t;
		return this;
	}
	public SauteedRecipeBuilder removeNBT() {
		removeNBT=true;
		return this;
	}
	public SauteedRecipeBuilder perBowl(float num) {
		this.per=num;
		return this;
	}
	public SauteedRecipe end(Ingredient bowl) {
		return new SauteedRecipe(allow, deny, priority, time, output,removeNBT,per,bowl,model);
	}

	public SauteedRecipe finish(BiConsumer<ResourceLocation, IDataRecipe> out,Ingredient bowl) {
		SauteedRecipe r = end(bowl);
		out.accept(id,r);
		return r;
	}
}
