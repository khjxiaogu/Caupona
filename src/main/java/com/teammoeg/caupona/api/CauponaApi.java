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

package com.teammoeg.caupona.api;

import java.util.Optional;

import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CauponaApi {

	private CauponaApi() {
	}

	public static void apply(Level worldIn, LivingEntity entityLiving, IFoodInfo info) {
		if (!worldIn.isClientSide) {
			RandomSource r = entityLiving.getRandom();
			for (PossibleEffect ef : info.getEffects()) {
				if (r.nextFloat() < ef.probability())
					entityLiving.addEffect(ef.effect());
			}
			if (entityLiving instanceof Player player) {
				player.getFoodData().eat(info.getHealing(), info.getSaturation());
			}
		}
	}

	public static Optional<ItemStack> fillBowl(IFluidHandler handler) {
		FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
		if (stack.getAmount() == 250)
			return fillBowl(handler.drain(250, FluidAction.EXECUTE));
		return Optional.empty();
	}
	public static Optional<ItemStack> getFilledItemStack(IFluidHandler handler,ItemStack in) {
		FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
		ContanerContainFoodEvent ev=Utils.contain(in, stack,true);
		if (ev.isAllowed())
			return getFilledItemStack(handler.drain(ev.drainAmount, FluidAction.EXECUTE),in);
		return Optional.empty();
	}
	public static Optional<ItemStack> getFilledItemStack(FluidStack stack,ItemStack in) {
		ContanerContainFoodEvent ev=Utils.contain(in, stack,false);
		if (ev.isAllowed())
			return Optional.of(ev.out);
		return Optional.empty();
		
	}
	public static Optional<ItemStack> fillBowl(FluidStack stack) {
		if (stack.getAmount() != 250)
			return Optional.empty();
		RecipeHolder<BowlContainingRecipe> recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
		if (recipe != null) {
			ItemStack ret = recipe.value().handle(stack);
			return Optional.of(ret);
		}
		return Optional.empty();
	}

	public static Optional<ItemStack> getBlockFilledItemStack(Fluid f, ItemStack is) {
		FluidStack stack=new FluidStack(f,250);
		ContanerContainFoodEvent ev=Utils.containBlock(is, stack);
		if (ev.isAllowed())
			return Optional.of(ev.out);
		return Optional.empty();
	}
}
