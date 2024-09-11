package com.teammoeg.caupona.item;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.components.SauteedFoodInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class LoafDishItem extends DishItem{

	public LoafDishItem(DishBlock block,Properties properties) {
		super(block, properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		@Nullable SauteedFoodInfo info = stack.get(CPCapability.SAUTEED_INFO);
		if(info==null)return null;
		return info.getFood(5,3).build();
	}



}
