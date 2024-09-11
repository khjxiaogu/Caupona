package com.teammoeg.caupona.item;

import java.util.function.Supplier;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.StewInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class LoafBowlItem extends StewItem{

	public LoafBowlItem(Block block, Supplier<Fluid> fluid, Properties properties) {
		super(block, fluid, properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		StewInfo info = stack.get(CPCapability.STEW_INFO);
		if(info==null)return null;
		return info.getFood(5,3).build();
	}



}
