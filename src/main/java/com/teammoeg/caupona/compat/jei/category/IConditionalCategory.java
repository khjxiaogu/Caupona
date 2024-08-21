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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.api.events.DrawCustomConditionEvent;
import com.teammoeg.caupona.api.events.EventResult;
import com.teammoeg.caupona.data.recipes.IConditionalRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.NeoForge;

public abstract class IConditionalCategory<T extends IConditionalRecipe&Recipe<?>> implements IRecipeCategory<RecipeHolder<T>> {
	private IDrawable BACKGROUND;
	private IGuiHelper helper;
	public static IDrawable COND_NONE;
	public static IDrawable COND_LIST;
	public static IDrawable COND_EXCEPT;
	public static IDrawable COND_HALF;
	public static IDrawable COND_MAINLY;
	public static IDrawable COND_MAINLY_LIST;
	public static IDrawable COND_HALF_LIST;
	public static IDrawable POT_HEADING;
	public static IDrawable PAN_HEADING;
	public static IDrawable[] DENSITY;
	public static IDrawable DETAIL;
	public IConditionalCategory(IGuiHelper guiHelper) {
		this.BACKGROUND = guiHelper.createBlankDrawable(100, 105);
		this.helper = guiHelper;
		
	}
	public static void init(IGuiHelper helper) {
		COND_NONE=grid(helper,4,0);
		COND_EXCEPT=grid(helper,0,0);
		COND_HALF=grid(helper,1,0);
		COND_LIST=grid(helper,2,0);
		COND_MAINLY=grid(helper,3,0);
		COND_HALF_LIST=grid(helper,0,1);
		COND_MAINLY_LIST=grid(helper,1,1);
		PAN_HEADING=grid(helper,0,2,100,52);
		POT_HEADING=grid(helper,0,3,100,52);
		DENSITY=new IDrawable[5];
		DENSITY[0]=grid(helper,0,4,26*0,0,26,16);
		DENSITY[1]=grid(helper,0,4,26*1,0,26,16);
		DENSITY[2]=grid(helper,0,4,26*2,0,26,16);
		DENSITY[3]=grid(helper,0,4,26*3,0,26,16);
		DENSITY[4]=grid(helper,0,4,26*4,0,26,16);
		DETAIL=grid(helper,3,1,20,20);
	}
	public static IDrawable grid(IGuiHelper helper,int x,int y) {
		return grid(helper,x,y,47, 53);
	}
	public static IDrawable grid(IGuiHelper helper,int x,int y,int w,int h) {
		return grid(helper,x,y,0,0,w,h);
	}
	public static IDrawable grid(IGuiHelper helper,int x,int y,int dx,int dy,int w,int h) {
		return helper.createDrawable(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"textures/gui/recipes/elements/recipe_page_elements.png"), x*47+dx, y*53+dy, w, h);
	}
	public abstract IDrawable getHeadings();
	public abstract void drawCustom(RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,double mouseY);
	@Override
	public void draw(RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,double mouseY) {
		getHeadings().draw(stack);
		drawConditionList(stack,recipe.value().getAllow(),1,52);
		drawConditionList(stack,recipe.value().getDeny(),52,52);
		drawCustom(recipe,recipeSlotsView,stack,mouseX,mouseY);
	}
	public boolean drawConditionList(GuiGraphics stack,List<IngredientCondition> conditions,int offX,int offY) {
		if(conditions==null||conditions.isEmpty()) {
			COND_NONE.draw(stack,offX,offY);
		}else {
			if(NeoForge.EVENT_BUS.post(new DrawCustomConditionEvent(helper, conditions, stack, offX, offY)).getResult()==EventResult.DENY)return false;
			COND_LIST.draw(stack,offX,offY);
			DETAIL.draw(stack,offX+13,offY+14);
		}
		return false;
		
	}
	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}
	public static boolean inRange(double x, double y, int ox, int oy, int w, int h) {
		return x > ox && x < ox + w && y > oy && y < oy + h;
	}

	@Override
	public List<Component> getTooltipStrings(RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView, double mouseX,
			double mouseY) {
		if (inRange(mouseX, mouseY, 0, 50, 100, 50)) {
			List<Component> allowence = null;
			List<IngredientCondition> conds;
			if (mouseX < 50)
				conds = recipe.value().getAllow();
			else
				conds = recipe.value().getDeny();
			if (conds != null)
				allowence = conds.stream().map(e -> e.getTranslation(GameTranslation.get())).map(Utils::string)
						.collect(Collectors.toList());
			if (allowence != null && !allowence.isEmpty()) {
				if (mouseX < 50)
					allowence.add(0, Utils.translate("recipe.caupona.allow"));
				else
					allowence.add(0, Utils.translate("recipe.caupona.deny"));
				return allowence;
			}

		}
		return Arrays.asList();
	}


}
