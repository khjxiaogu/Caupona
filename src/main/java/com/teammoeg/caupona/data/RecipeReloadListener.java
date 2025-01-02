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

package com.teammoeg.caupona.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.baseconditions.BaseConditions;
import com.teammoeg.caupona.data.recipes.conditions.Conditions;
import com.teammoeg.caupona.data.recipes.numbers.Numbers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeReloadListener implements ResourceManagerReloadListener {
	ReloadableServerResources data;
	public static final Logger logger = LogManager.getLogger(CPMain.MODNAME + " recipe generator");

	public RecipeReloadListener(ReloadableServerResources dpr) {
		data = dpr;
	}

	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
		buildRecipeLists(data.getRecipeManager());
	}

	RecipeManager clientRecipeManager;

	/**
	 * @param event  
	 */
	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		if (FoodValueRecipe.recipeset != null)
			FoodValueRecipe.recipeset.forEach(FoodValueRecipe::clearCache);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRecipesUpdated(RecipesUpdatedEvent event) {
		buildRecipeLists(event.getRecipeManager());
	}

	static int generated_fv = 0;
	
	private static FoodValueRecipe addCookingTime(Item i, ItemStack iis,Set<Item> added, List<SmokingRecipe> irs, boolean force) {
		if (FoodValueRecipe.recipes.containsKey(i))
			return FoodValueRecipe.recipes.get(i);
		added.add(i);
		for (SmokingRecipe sr : irs) {
			if(sr.getIngredients().size()>0)
			if (sr.getIngredients().get(0).test(iis)) {
				SimpleContainer fake=new SimpleContainer(3);
				fake.setItem(0,iis);
				ItemStack reslt = sr.assemble(fake,RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
				if (DissolveRecipe.recipes.stream().anyMatch(e -> e.test(reslt)))
					continue;
				if(added.contains(reslt.getItem()))
					break;
				FoodValueRecipe ret = addCookingTime(reslt.getItem(), reslt,added, irs, true);
				FoodProperties of = reslt.getFoodProperties(null);
				if (of != null && of.getNutrition() > ret.heal) {
					ret.effects = of.getEffects();
					ret.heal = of.getNutrition();
					ret.sat = of.getSaturationModifier();
					ret.setRepersent(iis);
				}
				FoodValueRecipe.recipes.put(i, ret);
				ret.processtimes.put(i, sr.getCookingTime() + ret.processtimes.getOrDefault(reslt.getItem(), 0));
				return ret;
			}
		}
		if (force) {
			FoodProperties of = iis.getFoodProperties(null);
			FoodValueRecipe ret = FoodValueRecipe.recipes.computeIfAbsent(i,
					e -> new FoodValueRecipe(new ResourceLocation(CPMain.MODID, "food/generated/" + (generated_fv++)), 0,
							0, iis, e));
			if (of != null && of.getNutrition() > ret.heal) {
				ret.effects = of.getEffects();
				ret.heal = of.getNutrition();
				ret.sat = of.getSaturationModifier();
				ret.setRepersent(iis);
			}
			return ret;
		}
		return null;
	}

	public static void buildRecipeLists(RecipeManager recipeManager) {

		Collection<Recipe<?>> recipes = recipeManager.getRecipes();
		if (recipes.size() == 0)
			return;
	
		logger.info("Building recipes...");
		Stopwatch sw = Stopwatch.createStarted();
		Conditions.clearCache();
		Numbers.clearCache();
		BaseConditions.clearCache();
		BowlContainingRecipe.recipes = filterRecipes(recipes, BowlContainingRecipe.class, BowlContainingRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.fluid, UnaryOperator.identity()));

		FoodValueRecipe.recipes = filterRecipes(recipes, FoodValueRecipe.class, FoodValueRecipe.TYPE)
				.flatMap(t -> t.processtimes.keySet().stream().map(i -> new Pair<>(i, t)))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		List<SmokingRecipe> irs = recipeManager.getAllRecipesFor(RecipeType.SMOKING);

		DissolveRecipe.recipes = filterRecipes(recipes, DissolveRecipe.class, DissolveRecipe.TYPE)
				.collect(Collectors.toList());

		BoilingRecipe.recipes = filterRecipes(recipes, BoilingRecipe.class, BoilingRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.before, UnaryOperator.identity()));

		FluidFoodValueRecipe.recipes = filterRecipes(recipes, FluidFoodValueRecipe.class, FluidFoodValueRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.f, UnaryOperator.identity()));

		StewCookingRecipe.sorted = filterRecipes(recipes, StewCookingRecipe.class, StewCookingRecipe.TYPE).collect(Collectors.toList());
		StewCookingRecipe.sorted.sort((t2, t1) -> t1.getPriority() - t2.getPriority());
		StewCookingRecipe.cookables = StewCookingRecipe.sorted.stream().flatMap(StewCookingRecipe::getAllNumbers).collect(Collectors.toSet());
		
		SauteedRecipe.sorted = filterRecipes(recipes, SauteedRecipe.class, SauteedRecipe.TYPE).collect(Collectors.toList());
		SauteedRecipe.sorted.sort((t2, t1) -> t1.getPriority() - t2.getPriority());
		SauteedRecipe.cookables = SauteedRecipe.sorted.stream().flatMap(SauteedRecipe::getAllNumbers).collect(Collectors.toSet());

		CountingTags.tags = Stream
			.concat(Stream
				.concat(filterRecipes(recipes, CountingTags.class, CountingTags.TYPE).flatMap(r -> r.tag.stream()),
						StewCookingRecipe.sorted.stream().flatMap(StewCookingRecipe::getTags)),SauteedRecipe.sorted.stream().flatMap(SauteedRecipe::getTags)
				)
				.collect(Collectors.toSet());
		// CountingTags.tags.forEach(System.out::println);



		DoliumRecipe.recipes = filterRecipes(recipes, DoliumRecipe.class, DoliumRecipe.TYPE)
				.collect(Collectors.toList());
		DoliumRecipe.recipes
				.sort(((Comparator<DoliumRecipe>) (c1, c2) -> Integer.compare(c2.items.size(), c1.items.size()))
						.thenComparing((c1, c2) -> Integer.compare(
								c2.items.stream().reduce(0, (a, b) -> a + b.getSecond(), (a, b) -> a + b),
								c1.items.stream().reduce(0, (a, b) -> a + b.getSecond(), (a, b) -> a + b))));

		AspicMeltingRecipe.recipes = filterRecipes(recipes, AspicMeltingRecipe.class, AspicMeltingRecipe.TYPE)
				.collect(Collectors.toList());

		SpiceRecipe.recipes = filterRecipes(recipes, SpiceRecipe.class, SpiceRecipe.TYPE).collect(Collectors.toList());
		Set<Item> is=new HashSet<>();
		for (Item i : ForgeRegistries.ITEMS) {
			ItemStack iis = new ItemStack(i);
			if (FoodValueRecipe.recipes.containsKey(i))
				continue;
			if (DissolveRecipe.recipes.stream().anyMatch(e -> e.test(iis)))
				continue;
			addCookingTime(i, iis,is, irs, false);
		}

		FoodValueRecipe.recipeset = new HashSet<>(FoodValueRecipe.recipes.values());

		sw.stop();
		logger.info("Recipes built, cost {}", sw);
	}

	static <R extends Recipe<?>> Stream<R> filterRecipes(Collection<Recipe<?>> recipes, Class<R> recipeClass,
			RegistryObject<RecipeType<Recipe<?>>> recipeType) {
		return recipes.stream().filter(iRecipe -> iRecipe.getType() == recipeType.get()).map(recipeClass::cast);
	}
}
