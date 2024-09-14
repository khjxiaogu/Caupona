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
