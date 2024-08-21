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
