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
