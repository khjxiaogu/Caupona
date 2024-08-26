package com.teammoeg.caupona.util;

import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;

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
        return Utils.extractFluid(container);
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