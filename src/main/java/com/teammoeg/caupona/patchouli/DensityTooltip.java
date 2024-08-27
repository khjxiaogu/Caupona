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

package com.teammoeg.caupona.patchouli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class DensityTooltip implements ICustomComponent {
	int x, y, w, h;
	IVariable recipe;
	transient List<Component> density;

	public DensityTooltip() {
	}

	@SuppressWarnings("resource")
	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
		recipe = lookup.apply(recipe);
		ResourceLocation out = ResourceLocation.parse(recipe.asString());
		Recipe<?> r = Minecraft.getInstance().level.getRecipeManager().byKey(out).map(t->t.value()).orElse(null);
		if (r instanceof StewCookingRecipe cr) {
			density = new ArrayList<>();
			density.add(Utils.translate("recipe.caupona.density", cr.getDensity()));
		}
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
	}

	@Override
	public void render(GuiGraphics ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		if (context.isAreaHovered(mouseX, mouseY, x, y, w, h))
			context.setHoverTooltipComponents(density);
	}

}
