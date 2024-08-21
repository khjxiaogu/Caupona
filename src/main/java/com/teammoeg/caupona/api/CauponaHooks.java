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

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class CauponaHooks {

	private CauponaHooks() {
	}

	public static final ResourceLocation stew = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "stews");

	public static Optional<List<FloatemStack>> getItems(ItemStack stack) {
		IFoodInfo fi=CPCapability.FOOD_INFO.getCapability(stack, null);
		if (fi!=null) {
			return Optional.of(fi.getStacks());
		}
		@Nullable IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (cap!=null) {
			IFluidHandlerItem data = cap;
			FluidStack fs = data.getFluidInTank(0);
			// TODO: CHECK STEW TAG
			return Optional.of(Utils.getOrCreateInfo(fs).getStacks());
		}
		return Optional.empty();
	}

	public static Fluid getBase(ItemStack stack) {
		@Nullable IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (cap!=null) {
			IFluidHandlerItem data = cap;
			return Utils.getOrCreateInfo(data.getFluidInTank(0)).base;
		}else {
			@Nullable StewInfo data=stack.get(CPCapability.STEW_INFO);
			if(data!=null)
				return data.base;
		}
		return Fluids.EMPTY;
	}

	public static Optional<IFoodInfo> getInfo(ItemStack stack) {
		IFoodInfo fi=CPCapability.FOOD_INFO.getCapability(stack, null);
		if (fi!=null) {
			return Optional.of(fi);
		}
		@Nullable IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (cap!=null) {
			IFluidHandlerItem data = cap;
			return Optional.of(Utils.getOrCreateInfo(data.getFluidInTank(0)));
		}
		return Optional.empty();
	}
	
}
