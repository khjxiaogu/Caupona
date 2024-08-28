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

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class BoilingCategory implements IRecipeCategory<RecipeHolder<BoilingRecipe>> {
	@SuppressWarnings("rawtypes")
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CPMain.MODID, "boiling",RecipeHolder.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public BoilingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "nail_soup"))));
		ResourceLocation guiMain = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/gui/jei/boiling.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);

	}



	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".boiling.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(RecipeHolder<BoilingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.value().time / 20f) + "s";
		stack.drawString(Minecraft.getInstance().font, burnTime, 103, 55, 0xFFFFFF);

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
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BoilingRecipe> recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 9)
				.addIngredients(NeoForgeTypes.FLUID_STACK, unpack(recipe.value().before, 250))
				.setFluidRenderer(1250, false, 16, 46);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 9)
				.addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(recipe.value().after, 250))
				.setFluidRenderer(1250, false, 16, 46);
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecipeType<RecipeHolder<BoilingRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}

}
