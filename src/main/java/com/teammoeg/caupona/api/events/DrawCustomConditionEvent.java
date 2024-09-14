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

package com.teammoeg.caupona.api.events;

import java.util.List;

import com.teammoeg.caupona.data.recipes.IngredientCondition;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;

@OnlyIn(Dist.CLIENT)
public class DrawCustomConditionEvent extends Event{
	IGuiHelper guihelper;
	List<IngredientCondition> conditions;
	GuiGraphics stack;
	int xOffset;
	int yOffset;
	EventResult result;
	public DrawCustomConditionEvent(IGuiHelper guihelper, List<IngredientCondition> conditions, GuiGraphics stack,
			int xOffset, int yOffset) {
		super();
		this.guihelper = guihelper;
		this.conditions = conditions;
		this.stack = stack;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	public EventResult getResult() {
		return result;
	}
	public void setResult(EventResult result) {
		this.result = result;
	}
}
