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

package com.teammoeg.caupona.util;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.components.ItemHoldedFluidData;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class FluidItemWrapper implements IFluidHandlerItem
{

    protected ItemStack container;

    public FluidItemWrapper(ItemStack container)
    {
        this.container = container;
    }
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    public FluidStack getFluid()
    {
        @Nullable ItemHoldedFluidData comp=container.get(CPCapability.ITEM_FLUID);
        if(comp==null)
        	return FluidStack.EMPTY;
        FluidStack fs=new FluidStack(comp.fluidType(),250);
        fs.applyComponents(container.getComponentsPatch());
        fs.remove(CPCapability.ITEM_FLUID);
        return fs;
    }

    protected void setFluid(FluidStack fluidStack)
    {
    	if(fluidStack.isEmpty()) {
    		container=container.getCraftingRemainingItem();
    		return;
    	}
        ContanerContainFoodEvent ev=Utils.contain(container.getCraftingRemainingItem(), fluidStack, false);
        if(ev.isAllowed())
        	container=ev.out;
    }
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return false;
	}
	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (container.getCount() != 1 || resource.getAmount() < 250)
        {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty() && FluidStack.isSameFluidSameComponents(fluidStack,resource))
        {
            if (action.execute())
            {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
	}
	@Override
	public int getTanks() {
		return 1;
	}
	@Override
	public int getTankCapacity(int tank) {
		return 250;
	}
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (container.getCount() != 1 || maxDrain < 250)
        {
            return FluidStack.EMPTY;
        }

        FluidStack fluidStack = getFluid();
        if (!fluidStack.isEmpty())
        {
            if (action.execute())
            {
                setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }

        return FluidStack.EMPTY;
	}

}