package com.teammoeg.caupona.util;

import java.util.function.Consumer;

import net.minecraft.world.food.FoodProperties;

public class FoodMaterialInfo {
	public String name;
	public float composite;
	public FoodProperties.Builder food;
	public FoodMaterialInfo(String name, int heal, float sat) {
		super();
		this.name = name;
		food=new FoodProperties.Builder();
		food.nutrition(heal);
		food.saturationModifier(sat);
	}
	public FoodMaterialInfo(String name, float composite) {
		super();
		this.name = name;
		this.composite = composite;
	}
	public FoodMaterialInfo(String name, int heal, float sat, float composite) {
		this(name,heal,sat);
		this.composite = composite;
	}
	public FoodMaterialInfo food(Consumer<FoodProperties.Builder> cons) {
		cons.accept(food);
		return this;
	}
}
