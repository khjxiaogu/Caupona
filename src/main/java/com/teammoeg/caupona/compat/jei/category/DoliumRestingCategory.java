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
import java.util.Arrays;
import java.util.List;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class DoliumRestingCategory implements IRecipeCategory<RecipeHolder<DoliumRecipe>> {
	@SuppressWarnings("rawtypes")
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CPMain.MODID, "dolium_resting",RecipeHolder.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public DoliumRestingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPBlocks.dolium.get(0)));
		ResourceLocation guiMain = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/gui/jei/maximum_resting.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".resting.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(RecipeHolder<DoliumRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.value().time / 20f) + "s";
		stack.drawString(Minecraft.getInstance().font,  burnTime, 100, 55, 0xFFFFFF);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	private static List<ItemStack> unpack(SizedOrCatalystIngredient ps) {
		return Arrays.asList(ps.getItems());
	}
	private static List<FluidStack> unpack(SizedOrCatalystFluidIngredient ps) {
		List<FluidStack> sl = new ArrayList<>();
		for (FluidStack is : ps.getFluids())
			sl.add(is.copy());
		return sl;
	}
	private static List<ItemStack> unpack(Ingredient ps) {
		if (ps.getClass()!=Ingredient.class)
			return Arrays.asList(ps.getItems());
		List<ItemStack> sl = new ArrayList<>();
		for (ItemStack is : ps.getItems())
			if (is.isDamageableItem())
				for (int i = 0; i <= is.getMaxDamage(); i++) {
					ItemStack iss = is.copy();
					iss.setDamageValue(i);
					sl.add(iss);
				}
			else
				sl.add(is);
		return sl;
	}

	private static RecipeIngredientRole type(SizedOrCatalystIngredient ps) {
		return ps.count() == 0 ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
	}

	private static class CatalistCallback implements IRecipeSlotRichTooltipCallback {
		int cnt;

		public CatalistCallback(int cnt) {
			super();
			this.cnt = cnt;
		}

		@Override
		public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
			if (cnt == 0)
				tooltip.add(Utils.translate("gui.jei.category.caupona.catalyst"));
		}

	};

	private static CatalistCallback cb(SizedOrCatalystIngredient ps) {
		return new CatalistCallback(ps.count());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DoliumRecipe> recipe, IFocusGroup focuses) {
		if (recipe.value().items.size() > 0) {
			builder.addSlot(type(recipe.value().items.get(0)), 4, 6)
					.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.value().items.get(0)))
					.addRichTooltipCallback(cb(recipe.value().items.get(0)));
			if (recipe.value().items.size() > 1) {
				builder.addSlot(type(recipe.value().items.get(1)), 4, 24)
						.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.value().items.get(1)))
						.addRichTooltipCallback(cb(recipe.value().items.get(1)));
				if (recipe.value().items.size() > 2) {
					builder.addSlot(type(recipe.value().items.get(2)), 4, 42)
							.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.value().items.get(2)))
							.addRichTooltipCallback(cb(recipe.value().items.get(2)));
				}
			}
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 24).addIngredient(VanillaTypes.ITEM_STACK, recipe.value().output);
		if (recipe.value().extra != null) {
			builder.addSlot(RecipeIngredientRole.INPUT, 89, 10).addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.value().extra));
		}
		if (recipe.value().fluid!=null)
			builder.addSlot(RecipeIngredientRole.INPUT, 26, 9)
					.addIngredients(NeoForgeTypes.FLUID_STACK, unpack(recipe.value().fluid))
					.setFluidRenderer(1250, false, 16, 46)
					.addRichTooltipCallback(new BaseCallback(recipe.value().base, recipe.value().density));

	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecipeType<RecipeHolder<DoliumRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}

}
