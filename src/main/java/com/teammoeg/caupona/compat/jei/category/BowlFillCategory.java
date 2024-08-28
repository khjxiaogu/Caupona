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

package com.teammoeg.caupona.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class BowlFillCategory implements IRecipeCategory<RecipeHolder<BowlContainingRecipe>> {
	@SuppressWarnings("rawtypes")
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CPMain.MODID, "bowl_filling",RecipeHolder.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public BowlFillCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPItems.water_bowl.get()));
		ResourceLocation guiMain = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/gui/jei/container_filling.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".filling.title");
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}
	private static List<FluidStack> unpack(FluidIngredient ps,int amount) {
		List<FluidStack> sl = new ArrayList<>();
		for (FluidStack is : ps.getStacks())
			sl.add(is.copyWithAmount(amount));
		return sl;
	}
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BowlContainingRecipe> recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 28).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(recipe.value().bowl));
		builder.addSlot(RecipeIngredientRole.INPUT, 56, 14).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.BOWL));
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 9)
				.addIngredients(NeoForgeTypes.FLUID_STACK, unpack(recipe.value().fluid,250))
				.setFluidRenderer(250, true, 16, 46);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecipeType<RecipeHolder<BowlContainingRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}

}
