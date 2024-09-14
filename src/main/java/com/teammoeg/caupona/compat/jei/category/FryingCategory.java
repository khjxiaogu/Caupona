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

package com.teammoeg.caupona.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FryingCategory extends IConditionalCategory<SauteedRecipe> {
	@SuppressWarnings("rawtypes")
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CPMain.MODID, "frying",RecipeHolder.class);
	private IDrawable ICON;
	private IGuiHelper helper;

	public FryingCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPItems.gravy_boat.get()));
		this.helper = guiHelper;
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".frying.title");
	}

	@Override
	public void draw(RecipeHolder<SauteedRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guistack, double mouseX,
			double mouseY) {
		PoseStack stack=guistack.pose();
		ResourceLocation imagePath=ResourceLocation.fromNamespaceAndPath(recipe.id().getNamespace(),"textures/gui/recipes/" + recipe.id().getPath() + ".png");
		if(Minecraft.getInstance().getResourceManager().getResource(imagePath).isPresent()) {
			stack.pushPose();
			stack.scale(0.5f, 0.5f, 0);
			helper.createDrawable(imagePath, 0, 0, 200, 210).draw(guistack);
			stack.popPose();
		}else super.draw(recipe, recipeSlotsView, guistack, mouseX, mouseY);
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SauteedRecipe> recipe, IFocusGroup focuses) {

		builder.addSlot(RecipeIngredientRole.INPUT, 30, 13).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(CPItems.gravy_boat.get()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 18).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(recipe.value().output)).addRichTooltipCallback((v,t)->{t.add(Utils.translate("gui.jei.category.caupona.ingredientPer",recipe.value().count));});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecipeType<RecipeHolder<SauteedRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}


	@Override
	public IDrawable getHeadings() {
		return PAN_HEADING;
	}


	@Override
	public void drawCustom(RecipeHolder<SauteedRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
	}

}
