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
