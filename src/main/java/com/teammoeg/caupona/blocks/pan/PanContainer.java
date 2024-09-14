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

package com.teammoeg.caupona.blocks.pan;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.HidableSlot;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PanContainer extends CPBaseContainer<PanBlockEntity> {

	public PanContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (PanBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public PanContainer(int id, Inventory inv, PanBlockEntity blockEntity) {
		super(CPGui.PAN.get(),blockEntity, id,12);
		for (int i = 0; i < 9; i++)
			this.addSlot(new HidableSlot(blockEntity.inv, i, 62 + (i % 3) * 18, 13 + (i / 3) * 18, () -> blockEntity.processMax == 0));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 9, 147, 13));

		this.addSlot(new OutputSlot(blockEntity.inv, 10, 136, 47));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 11, 125, 13) {

			@Override
			public boolean mayPlace(ItemStack stack) {
				if((!inv.player.getAbilities().instabuild&&blockEntity.isInfinite))return false;
				return super.mayPlace(stack);
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				if((!inv.player.getAbilities().instabuild&&blockEntity.isInfinite))return false;
				return super.mayPickup(playerIn);
			}});
		super.addPlayerInventory(inv,8,84,142);
	}


	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 9, 10, false)||this.moveItemStackTo(slotStack, 11, 12, false)||this.moveItemStackTo(slotStack, 0, 9, false);
	}
}
