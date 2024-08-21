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

package com.teammoeg.caupona.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.gui.DoliumScreen;
import com.teammoeg.caupona.client.gui.KitchenStoveScreen;
import com.teammoeg.caupona.client.gui.PanScreen;
import com.teammoeg.caupona.client.gui.PortableBrazierScreen;
import com.teammoeg.caupona.client.gui.StewPotScreen;
import com.teammoeg.caupona.compat.jei.category.BoilingCategory;
import com.teammoeg.caupona.compat.jei.category.BowlEmptyCategory;
import com.teammoeg.caupona.compat.jei.category.BowlFillCategory;
import com.teammoeg.caupona.compat.jei.category.BrazierCategory;
import com.teammoeg.caupona.compat.jei.category.DoliumRestingCategory;
import com.teammoeg.caupona.compat.jei.category.FryingCategory;
import com.teammoeg.caupona.compat.jei.category.IConditionalCategory;
import com.teammoeg.caupona.compat.jei.category.PotCategory;
import com.teammoeg.caupona.compat.jei.category.PotRestingCategory;
import com.teammoeg.caupona.compat.jei.category.StewCookingCategory;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@JeiPlugin
public class JEICompat implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "jei_plugin");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(CPItems.pbrazier.get()), BrazierCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.STEW_POT.get()), PotCategory.TYPE, BoilingCategory.TYPE,
				PotRestingCategory.TYPE, StewCookingCategory.TYPE);
		for (Block bl : CPBlocks.dolium)
			registration.addRecipeCatalyst(new ItemStack(bl), DoliumRestingCategory.TYPE, PotRestingCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.STONE_PAN.get()), FryingCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.COPPER_PAN.get()), FryingCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.IRON_PAN.get()), FryingCategory.TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(BrazierCategory.TYPE,new ArrayList<>(AspicMeltingRecipe.recipes));
		registration.addRecipes(PotCategory.TYPE,new ArrayList<>(AspicMeltingRecipe.recipes));
		registration.addRecipes(BoilingCategory.TYPE,new ArrayList<>(BoilingRecipe.recipes.values()));
		registration.addRecipes(BowlEmptyCategory.TYPE,new ArrayList<>(BowlContainingRecipe.recipes.values()));
		registration.addRecipes(BowlFillCategory.TYPE,new ArrayList<>(BowlContainingRecipe.recipes.values()));
		registration.addRecipes(DoliumRestingCategory.TYPE,new ArrayList<>(DoliumRecipe.recipes));
		registration.addRecipes(StewCookingCategory.TYPE,new ArrayList<>(StewCookingRecipe.sorted));
		registration.addRecipes(FryingCategory.TYPE,new ArrayList<>(SauteedRecipe.sorted));
		registration.addRecipes(PotRestingCategory.TYPE,
				DoliumRecipe.recipes.stream().filter(e -> e.value().items.size() == 0).collect(Collectors.toList())
				);
		registration.addItemStackInfo(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"leaden_walnut"))),Utils.translate("gui.jei.caupona.info.leaden_walnut"));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		IConditionalCategory.init(guiHelper);
		registration.addRecipeCategories(new BrazierCategory(guiHelper), new PotCategory(guiHelper),
				new BoilingCategory(guiHelper), new BowlEmptyCategory(guiHelper), new BowlFillCategory(guiHelper),
				new DoliumRestingCategory(guiHelper), new PotRestingCategory(guiHelper),
				new StewCookingCategory(guiHelper), new FryingCategory(guiHelper));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addGuiContainerHandler(PortableBrazierScreen.class, new IGuiContainerHandler<PortableBrazierScreen>() {
			@Override
			public Collection<IGuiClickableArea> getGuiClickableAreas(PortableBrazierScreen containerScreen,
					double mouseX, double mouseY) {
				IGuiClickableArea clickableArea1 = IGuiClickableArea.createBasic(60, 11, 14, 16, BrazierCategory.TYPE);
				IGuiClickableArea clickableArea2 = IGuiClickableArea.createBasic(90, 11, 14, 16, BrazierCategory.TYPE);
				return List.of(clickableArea1, clickableArea2);
			}
		});

		registry.addRecipeClickArea(DoliumScreen.class, 118, 32, 10, 25, DoliumRestingCategory.TYPE);
		registry.addRecipeClickArea(StewPotScreen.class, 132, 34, 38, 16, PotCategory.TYPE, BoilingCategory.TYPE,
				PotRestingCategory.TYPE, StewCookingCategory.TYPE);
		registry.addRecipeClickArea(KitchenStoveScreen.class, 61, 0, 54, 28, RecipeTypes.FUELING);
		registry.addRecipeClickArea(PanScreen.class, 125, 30, 38, 16, FryingCategory.TYPE);
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {

	}

}
