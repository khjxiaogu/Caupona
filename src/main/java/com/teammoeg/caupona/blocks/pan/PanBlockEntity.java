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

package com.teammoeg.caupona.blocks.pan;

import org.jetbrains.annotations.NotNull;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.PanPendingContext;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class PanBlockEntity extends CPBaseBlockEntity implements MenuProvider,IInfinitable,IFoodContainer {
	//process
	public int process;
	public int processMax;
	//work state
	public boolean working = false;
	public boolean operate = false;
	public boolean rsstate = false;
	
	boolean isInfinite = false;
	//output cache
	boolean removesNBT;
	public Item preout = Items.AIR;
	public ItemStack sout = ItemStack.EMPTY;
	public SauteedFoodInfo current;
	public int oamount;
	//Capabilities
	public ItemStackHandler inv = new ItemStackHandler(12) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 9)
				return SauteedRecipe.isCookable(stack);
			if (slot == 9) {
				return stack.is(Items.BOWL);
			}
			if (slot == 11)
				return SpiceRecipe.isValid(stack);
			return false;
		}

		@Override
		public int getSlotLimit(int slot) {
			if (slot < 9)
				return 1;
			return super.getSlotLimit(slot);
		}
	};
	public IItemHandler bowl = new IItemHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot < 9 || slot==10)
				return stack;
			return inv.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 9 || slot == 11)
				return ItemStack.EMPTY;
			if(slot<9&&inv.isItemValid(slot, inv.getStackInSlot(slot)))
				return ItemStack.EMPTY;
			ItemStack item=inv.extractItem(slot, amount, simulate);
			if(slot==10&&!item.isEmpty()&&sout.isEmpty())
				syncData();
			return item;
		}

		@Override
		public int getSlots() {
			return inv.getSlots();
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return inv.getStackInSlot(slot);
		}

		@Override
		public int getSlotLimit(int slot) {
			return inv.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if(slot<9||slot==10)
				return false;
			return inv.isItemValid(slot, stack);
		}
	};
	RangedWrapper ingredient = new RangedWrapper(inv, 0, 10) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	LazyOptional<IItemHandler> up = LazyOptional.of(() -> ingredient);
	LazyOptional<IItemHandler> side = LazyOptional.of(() -> bowl);
	public PanBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.PAN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		working = nbt.getBoolean("working");
		operate = nbt.getBoolean("operate");
		rsstate = nbt.getBoolean("rsstate");
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		
		if (nbt.contains("sout"))
			sout = ItemStack.of(nbt.getCompound("sout"));
		else
			sout = ItemStack.EMPTY;
		inv.deserializeNBT(nbt.getCompound("items"));
		if (!isClient) {
			isInfinite =nbt.getBoolean("inf");
			oamount = nbt.getInt("amount");
			if (nbt.contains("cur"))
				current = new SauteedFoodInfo(nbt.getCompound("cur"));
			else
				current = null;
			removesNBT=nbt.getBoolean("removeNbt");
		}
		preout = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("out")));

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putBoolean("working", working);
		nbt.putBoolean("operate", operate);
		nbt.putBoolean("rsstate", rsstate);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.put("sout", sout.serializeNBT());
		nbt.put("items", inv.serializeNBT());
		if (!isClient) {
			nbt.putInt("amount", oamount);
			nbt.putBoolean("inf",isInfinite);
			if (current != null)
				nbt.put("cur", current.save());
			nbt.putBoolean("removeNbt",removesNBT);
		}
		nbt.putString("out", Utils.getRegistryName(preout).toString());

	}

	private ItemStack tryAddSpice(ItemStack fs) {
		ItemStack ospi = inv.getStackInSlot(11);
		ItemStack spi=ospi;
		SpiceRecipe spice = SpiceRecipe.find(spi);
		if(this.getBlockState().is(CPBlocks.LEAD_PAN.get())) {
			if(spice!=null&&spice.canReactLead) {
				spi=CPItems.getSapa();
				spice=SpiceRecipe.find(spi);
			}
		}
		if (spice != null && SpiceRecipe.getMaxUse(ospi) >= fs.getCount()) {
			SauteedFoodInfo si = DishItem.getInfo(fs);
			if (!isInfinite) 
				inv.setStackInSlot(11, SpiceRecipe.handle(ospi, fs.getCount()));
			si.addSpice(spice.effect, spi);
			DishItem.setInfo(fs, si);
		}
		return fs;
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
			working = false;
			if (processMax > 0) {
				if (level.getBlockEntity(worldPosition.below()) instanceof IStove stove) {
					int rh =stove.requestHeat();
					process += rh;
					if (rh > 0) {
						working = true;
						this.syncData();
					}
					if (process >= processMax) {
						process = 0;
						processMax = 0;
						doWork();
						this.setChanged();
					}
				} else
					return;

			} else if (!sout.isEmpty()) {
				operate = false;
				if (inv.getStackInSlot(10).isEmpty()) {
					if(!isInfinite)
						inv.setStackInSlot(10, tryAddSpice(sout.split(1)));
					else
						inv.setStackInSlot(10, tryAddSpice(ItemHandlerHelper.copyStackWithSize(sout, 1)));
					this.setChanged();
				}
			} else {
				prepareWork();
			}
		}
		
	}

	private void prepareWork() {
		if (rsstate && !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate) {
			operate = false;
			if (!(level.getBlockEntity(worldPosition.below()) instanceof IStove stove) || !stove.canEmitHeat())
				return;
			make();
		}
	}

	private void doWork() {
		ItemStack is = new ItemStack(preout, oamount);
		if(!removesNBT)
			DishItem.setInfo(is, current);
		removesNBT=false;
		current = null;
		oamount = 0;
		preout = Items.AIR;
		sout = is;
	}

	@SuppressWarnings("resource")
	private void make() {
		//Do simulation requirement check
		//Ensure everything cookable
		int itms = 0;
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				if (SauteedRecipe.isCookable(is))
					itms++;
				else
					return;
			}
		}
		if (itms <= 0)
			return;
		//ensure has oil
		BlockPos oilProvidingPos=null;
		for (Direction d : Utils.horizontals) {
			BlockPos bp = this.getBlockPos().relative(d);
			BlockState bs = this.getLevel().getBlockState(bp);
			if (bs.is(CPBlocks.GRAVY_BOAT.get())) {
				int oil = GravyBoatBlock.getOil(bs);
				if (oil > 0) {
					//
					oilProvidingPos=bp;
					break;
				}
			}
		}
		if (oilProvidingPos==null)
			return;
		if (inv.getStackInSlot(9).isEmpty())return;
		//Draw items
		NonNullList<ItemStack> interninv = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				for (int j = 0; j < 9; j++) {
					ItemStack ois = interninv.get(j);
					if (ois.isEmpty()) {
						interninv.set(j, is.copy());
						break;
					} else if (ItemStack.isSameItemSameTags(ois, is)) {
						ois.setCount(ois.getCount() + is.getCount());
						break;
					}
				}
				//inv.setStackInSlot(i, is.getCraftingRemainingItem());
			}
		}
		//Make Pending Context
		int tpt = 0;
		SauteedFoodInfo current = new SauteedFoodInfo();
		for (int i = 0; i < 9; i++) {
			ItemStack is = interninv.get(i);
			if (is.isEmpty())
				break;
			current.addItem(is);
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
			if (fvr != null)
				tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
		}
		interninv.clear();
		current.completeAll();
		current.recalculateHAS();
		PanPendingContext ctx = new PanPendingContext(current);
		//Do recipe check
		float tcount=0;
		
		Item preout=Items.AIR;
		int processMax=0;
		boolean removesNBT=false;
		for (SauteedRecipe cr : SauteedRecipe.sorted) {
			if (cr.matches(ctx)) {
				processMax = Math.max(cr.time, tpt);
				preout = cr.output;
				removesNBT=cr.removeNBT;
				tcount=cr.count;
				break;
			}
		}
		if(preout==Items.AIR)return;
		if(tcount<=0)tcount=2f;
		int cook = Mth.ceil(itms / tcount);
		if (inv.getStackInSlot(9).getCount() < cook)
			return;
		
		//Complete simulation check, Start taking effect
		GravyBoatBlock.drawOil(getLevel(), oilProvidingPos, 1);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				inv.setStackInSlot(i, is.getCraftingRemainingItem());
			}
		}
		this.processMax = process = 0;
		tpt = Math.max(CPConfig.SERVER.fryTimeBase.get(), tpt);
		current.setParts(cook);
		current.completeAll();
		current.recalculateHAS();
		this.current=current;
		this.preout=preout;
		this.processMax=processMax;
		this.removesNBT=removesNBT;
		inv.getStackInSlot(9).shrink(cook);
		oamount = cook;
		if (this.getBlockState().is(CPBlocks.STONE_PAN.get()))
			tpt *= 2;
		processMax = tpt;
		this.syncData();
		return;
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new PanContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".pan.title");
	}
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == Direction.UP)
				return up.cast();
			return this.side.cast();
		}
		return super.getCapability(cap, side);
	}

	public ItemStackHandler getInv() {
		return inv;
	}

	@Override
	public boolean setInfinity() {
		return isInfinite=!isInfinite;
	}

	@Override
	public ItemStack getInternal(int num) {
		ItemStack result=inv.extractItem(10, 1,true);
		return result;
	}

	@Override
	public void setInternal(int num, ItemStack is) {
		inv.extractItem(10, 1, false);
		is=inv.insertItem(9, is,false);
		this.syncData();
		Utils.dropToWorld(this.getLevel(), is, this.getBlockPos());
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemStack is) {
		return is.is(Items.BOWL);
	}
}
