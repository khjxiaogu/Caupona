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

package com.teammoeg.caupona.blocks.stove;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.container.CPBaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KitchenStoveContainer extends CPBaseContainer<KitchenStoveBlockEntity> {

	public KitchenStoveContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (KitchenStoveBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public KitchenStoveContainer(int id, Inventory inv, KitchenStoveBlockEntity blockEntity) {
		super(CPGui.STOVE.get(),blockEntity,id,1);
		this.addSlot(new Slot(blockEntity, 0, 80, 55) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getBurnTime(null) > 0;
			}
		});
		super.addPlayerInventory(inv,8,84,142);
	}

	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, 1, false);
	}
}
