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

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class BrazierCategory implements IRecipeCategory<RecipeHolder<AspicMeltingRecipe>> {
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CPMain.MODID, "aspic_thawing_brazier",RecipeHolder.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public BrazierCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPItems.pbrazier.get()));
		ResourceLocation guiMain = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/gui/jei/aspic_thawing_brazier.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".thawing_brazier.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(RecipeHolder<AspicMeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.value().time / 20f) + "s";
		stack.drawString(Minecraft.getInstance().font,  burnTime, 103, 55, 0xFFFFFF);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AspicMeltingRecipe> recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 28).addIngredients(recipe.value().aspic);
		builder.addSlot(RecipeIngredientRole.INPUT, 57, 14).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(CPItems.water_bowl.get()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 9)
				.addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(recipe.value().fluid, recipe.value().amount))
				.setFluidRenderer(1250, false, 16, 46);
	}


	@Override
	public RecipeType<RecipeHolder<AspicMeltingRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}

}
