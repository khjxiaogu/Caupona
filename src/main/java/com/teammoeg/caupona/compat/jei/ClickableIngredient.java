package com.teammoeg.caupona.compat.jei;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.renderer.Rect2i;

public class ClickableIngredient<T> implements IClickableIngredient<T> {
	Rect2i area;
	ITypedIngredient<T> type;

	public ClickableIngredient(ITypedIngredient<T> type,Rect2i area) {
		this.area = area;
		this.type = type;
	}

	@Override
	public ITypedIngredient<T> getTypedIngredient() {
		return type;
	}

	@Override
	public Rect2i getArea() {
		return area;
	}

}
