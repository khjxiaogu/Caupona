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

package com.teammoeg.caupona.container;

import java.util.List;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTags.Items;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.util.INetworkContainer;
import com.teammoeg.caupona.util.ITickableContainer;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PortableBrazierContainer extends AbstractContainerMenu implements INetworkContainer, ITickableContainer {
	public static final int INGREDIENT = 0;
	private static final int CONTAINER = 1;
	private static final int FUEL = 2;
	private static final int OUT = 3;
	private final Player player;
	ItemStackHandler items = new ItemStackHandler(4) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot == INGREDIENT)
				return AspicMeltingRecipe.find(stack) != null;
			if (slot == CONTAINER)
				return stack.is(CPItems.water_bowl)||stack.is(CPItems.water_loaf_bowl);
			if (slot == FUEL)
				return stack.is(Items.PORTABLE_BRAZIER_FUEL_TYPE);
			return false;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};
	ItemStack bowl = ItemStack.EMPTY;
	ItemStack aspic = ItemStack.EMPTY;
	ItemStack out = ItemStack.EMPTY;
	ItemStack pout = ItemStack.EMPTY;// simulated output
	public int process;
	public int processMax;

	/**
	 * @param data  
	 */
	public PortableBrazierContainer(int id, Inventory playerInventory, FriendlyByteBuf data) {
		this(id, playerInventory);
	}

	public PortableBrazierContainer(int id, Inventory playerInventory) {
		super(CPGui.BRAZIER.get(), id);
		this.player = playerInventory.player;
		this.addSlot(new SlotItemHandler(items, INGREDIENT, 44, 11));
		this.addSlot(new SlotItemHandler(items, CONTAINER, 74, 11));
		this.addSlot(new SlotItemHandler(items, FUEL, 74, 44));
		addSlot(new OutputSlot(items, OUT, 104, 11));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 83 + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 141));

	}

	private void sendUpdate() {

		CompoundTag tag = new CompoundTag();
		tag.putInt("process", process);
		tag.putInt("processMax", processMax);
		sendMessage(tag);
	}

	/**
	 * Called when the container is closed.
	 */
	public void removed(Player pPlayer) {
		super.removed(pPlayer);

		if (!pPlayer.isAlive() || pPlayer instanceof ServerPlayer && ((ServerPlayer) pPlayer).hasDisconnected()) {
			for (int j = 0; j < items.getSlots(); ++j) {
				pPlayer.drop(items.getStackInSlot(j), false);
			}
			if (!bowl.isEmpty())
				pPlayer.drop(bowl, false);
			if (!aspic.isEmpty())
				pPlayer.drop(aspic, false);
			if (!out.isEmpty())
				pPlayer.drop(out, false);
		} else {
			Inventory inventory = pPlayer.getInventory();
			if (inventory.player instanceof ServerPlayer) {
				for (int i = 0; i < items.getSlots(); ++i) {
					inventory.placeItemBackInInventory(items.getStackInSlot(i));
				}
				if (!bowl.isEmpty())
					inventory.placeItemBackInInventory(bowl);
				if (!aspic.isEmpty())
					inventory.placeItemBackInInventory(aspic);
				if (!out.isEmpty())
					inventory.placeItemBackInInventory(out);
			}
		}

	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index == OUT) {
				if (!this.moveItemStackTo(slotStack, 4, 40, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index > OUT) {
				if (!this.moveItemStackTo(slotStack, 0, 3, false))
					if (index < 31) {
						if (!this.moveItemStackTo(slotStack, 31, 40, false))
							return ItemStack.EMPTY;
					} else if (index < 40 && !this.moveItemStackTo(slotStack, 6, 31, false))
						return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(slotStack, 6, 40, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;

	}

	@Override
	public void tick(boolean isServer) {
		if (isServer) {
			if (processMax > 0) {
				process++;
				if (process >= processMax) {
					out = Utils.insertToOutput(items, OUT, pout);
					process = 0;
					processMax = 0;
					bowl = ItemStack.EMPTY;
					aspic = ItemStack.EMPTY;
					pout = ItemStack.EMPTY;
				}
				sendUpdate();
			}
			if (!out.isEmpty()) {
				out = Utils.insertToOutput(items, OUT, out);
				return;
			}
			if (processMax == 0 && items.getStackInSlot(OUT).isEmpty() && !items.getStackInSlot(CONTAINER).isEmpty()
					&& items.getStackInSlot(FUEL).is(Items.PORTABLE_BRAZIER_FUEL_TYPE)) {
				AspicMeltingRecipe recipe = AspicMeltingRecipe.find(items.getStackInSlot(INGREDIENT));
				if (recipe != null) {
					FluidStack simulated=new FluidStack(recipe.fluid,250);
					List<RecipeHolder<BowlContainingRecipe>> recipeList=null;
					if(items.getStackInSlot(CONTAINER).is(CPItems.water_bowl))
						recipeList=BowlContainingRecipe.getRecipes(BowlContainingRecipe.WOODEN_BOWL);
					if(items.getStackInSlot(CONTAINER).is(CPItems.water_loaf_bowl))
						recipeList=BowlContainingRecipe.getRecipes(BowlContainingRecipe.BREAD_BOWL);
					if(recipeList!=null) {
						RecipeHolder<BowlContainingRecipe> recipe2 = recipeList.stream().filter(t->t.value().matches(simulated)).findFirst().orElse(null);
						if (recipe2 != null) {
							this.processMax = recipe.time;
							this.process = 0;
							bowl = items.getStackInSlot(CONTAINER).split(1);
							aspic = items.getStackInSlot(INGREDIENT).split(1);
							items.getStackInSlot(FUEL).shrink(1);
							pout = recipe2.value().handle(recipe.handle(aspic));
						}
					}
				}
			}
		}
	}

	@Override
	public ServerPlayer getOpenedPlayer() {
		return (ServerPlayer) player;
	}

	@Override
	public void handle(CompoundTag nbt) {

		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
	}
}