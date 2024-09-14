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

package com.teammoeg.caupona.blocks.pot;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.HidableSlot;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class StewPotContainer extends CPBaseContainer<StewPotBlockEntity> {
	public StewPotContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (StewPotBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public StewPotContainer(int id, Inventory inv, StewPotBlockEntity blockEntity) {
		super(CPGui.STEWPOT.get(),blockEntity, id,12);
		for (int i = 0; i < 9; i++)
			this.addSlot(new HidableSlot(blockEntity.getInv(), i, 45 + (i % 3) * 18, 17 + (i / 3) * 18, () -> blockEntity.proctype != 2));
		this.addSlot(new SlotItemHandler(blockEntity.getInv(), 9, 154, 17));

		this.addSlot(new OutputSlot(blockEntity.getInv(), 10, 143, 51));
		this.addSlot(new SlotItemHandler(blockEntity.getInv(), 11, 132, 17));
		super.addPlayerInventory(inv,8,84,142);
	}

	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 9, 10, false)||this.moveItemStackTo(slotStack, 11, 12, false)||this.moveItemStackTo(slotStack, 0, 9, false);

	}

}
