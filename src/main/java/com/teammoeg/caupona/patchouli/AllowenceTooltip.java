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

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.data.recipes.IConditionalRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

@OnlyIn(Dist.CLIENT)
public class AllowenceTooltip implements ICustomComponent {
	boolean allow;
	int x, y, w, h;
	IVariable recipe;
	transient List<Component> allowence;

	public AllowenceTooltip() {
	}

	@SuppressWarnings("resource")
	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
		recipe = lookup.apply(recipe);
		ResourceLocation out = ResourceLocation.parse(recipe.asString());
		Recipe<?> r = Minecraft.getInstance().level.getRecipeManager().byKey(out).map(t->t.value()).orElse(null);
		if (r instanceof IConditionalRecipe cr) {
			List<IngredientCondition> conds;
			if (allow)
				conds = cr.getAllow();
			else
				conds = cr.getDeny();
			if (conds != null)
				allowence = conds.stream().map(e -> e.getTranslation(GameTranslation.get())).map(Utils::string)
						.collect(Collectors.toList());
			if (allowence != null && !allowence.isEmpty()) {
				if (allow)
					allowence.add(0, Utils.translate("recipe.caupona.allow"));
				else
					allowence.add(0, Utils.translate("recipe.caupona.deny"));
			}
		}
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
	}

	@Override
	public void render(GuiGraphics ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		if (context.isAreaHovered(mouseX, mouseY, x, y, w, h))
			if (allowence != null && !allowence.isEmpty())
				context.setHoverTooltipComponents(allowence);
	}


}
