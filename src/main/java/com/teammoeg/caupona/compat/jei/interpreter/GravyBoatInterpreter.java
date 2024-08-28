package com.teammoeg.caupona.compat.jei.interpreter;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class GravyBoatInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final GravyBoatInterpreter INSTANCE=new GravyBoatInterpreter();
	private GravyBoatInterpreter() {
	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return ingredient.getDamageValue();
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return ""+ingredient.getDamageValue();
	}

}
