package com.teammoeg.caupona.api.events;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.fluids.FluidStack;

public class ContanerContainFoodEvent extends Event {
	public final ItemStack origin;
	public ItemStack out=ItemStack.EMPTY;
	public final FluidStack fs;
	public final int drainAmount;
	public final boolean isSimulated;
	public final boolean isBlockAccess;
	EventResult result;
	public ContanerContainFoodEvent(ItemStack origin, FluidStack fs,boolean isSimulated,boolean isBlockAccess) {
		super();
		this.origin = origin;
		this.fs = fs;
		this.drainAmount = fs.getAmount();
		this.isSimulated=isSimulated;
		this.isBlockAccess=isBlockAccess;
	}
	public boolean isAllowed() {
		return this.getResult()==EventResult.ALLOW&&!out.isEmpty();
	}
	public EventResult getResult() {
		return result;
	}
	public void setResult(EventResult result) {
		this.result = result;
	}
}
