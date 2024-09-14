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

package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DoliumContainer extends CPBaseContainer<CounterDoliumBlockEntity> {

	public DoliumContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (CounterDoliumBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public DoliumContainer(int id, Inventory inv, CounterDoliumBlockEntity blockEntity) {
		super(CPGui.DOLIUM.get(),blockEntity, id,6);
		this.addSlot(new SlotItemHandler(blockEntity.inv, 0, 153, 4));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 1, 134, 8));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 2, 115, 12));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 3, 132, 35));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 4, 132, 53));
		this.addSlot(new OutputSlot(blockEntity.inv, 5, 152, 51));
		addPlayerInventory(inv,8,83,141);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, 3, false)||this.moveItemStackTo(slotStack, 3, 5, false);
	}


}
