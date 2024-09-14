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

package com.teammoeg.caupona.container;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class PreviewSlot extends Slot {
	private static Container empty = new SimpleContainer(0);

	public PreviewSlot(int index, int xPosition, int yPosition) {
		super(empty, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	@Nonnull
	public abstract ItemStack getItem();

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void set(@Nonnull ItemStack stack) {
	}

	@Override
	public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		return 1;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return false;
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		return ItemStack.EMPTY;
	}

}
