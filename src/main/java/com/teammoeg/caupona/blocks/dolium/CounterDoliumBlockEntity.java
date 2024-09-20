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

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.SyncedFluidHandler;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public class CounterDoliumBlockEntity extends CPBaseBlockEntity implements MenuProvider, IInfinitable {
	ItemStackHandler inv = new ItemStackHandler(6) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 3)
				return DoliumRecipe.testInput(stack);
			if (slot == 3) {
				return SpiceRecipe.isValid(stack);
			}
			if (slot == 4) {
				return true;
			}
			return false;
		}

		@Override
		protected void onContentsChanged(int slot) {
			if(slot<5&&slot!=3)
				recipeHandler.onContainerChanged();
			setChanged();
			super.onContentsChanged(slot);
		}
		
	};
	public final FluidTank tank = new FluidTank(1250, f -> !f.getFluid().getFluidType().isLighterThanAir()) {

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			recipeHandler.onContainerChanged();
			recipeHandler.resetProgress();
			syncData();
		}

	};

	private FluidStack tryAddSpice(FluidStack fs) {
		SpiceRecipe spice = null;
		ItemStack spi = inv.getStackInSlot(3);
		if (fs.getAmount() % 250 == 0 && fs.getFluid() instanceof SoupFluid)
			spice = SpiceRecipe.find(spi);
		if (spice != null) {
			StewInfo si = Utils.getOrCreateInfo(fs);
			if (!si.canAddSpice())
				return fs;
			if (!isInfinite) {
				int consume = fs.getAmount() / 250;
				if (SpiceRecipe.getMaxUse(spi) < consume)
					return fs;
				inv.setStackInSlot(3, SpiceRecipe.handle(spi, consume));
			}
			si.addSpice(spice.effect, spi);
			Utils.setInfo(fs, si);
		}
		return fs;

	}
	public LazyTickWorker contain;
	boolean isInfinite = false;
	ItemStack inner = ItemStack.EMPTY;
	public final RecipeHandler<DoliumRecipe> recipeHandler=new RecipeHandler<>(()->{
		RecipeHolder<DoliumRecipe> recipe = DoliumRecipe.testDolium(tank.getFluid(), inv);
		if(recipe!=null) {
			inner=recipe.value().handleDolium(tank.getFluid(), inv);
		}
	});
	ResourceLocation lastRecipe;
	public CounterDoliumBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.DOLIUM.get(), pWorldPosition, pBlockState);
		contain = new LazyTickWorker(CPConfig.SERVER.containerTick.get(),()->{
			if (isInfinite) {
				FluidStack fs = tank.getFluid().copy();
				tryContianFluid();
				tank.setFluid(fs);
			} else {
				if(tryContianFluid())
					return true;
			}
			return false;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		recipeHandler.readCustomNBT(nbt, isClient);
		tank.readFromNBT(ra,nbt.getCompound("tank"));
		isInfinite = nbt.getBoolean("inf");
		if (!isClient) {
			inner = ItemStack.parseOptional(ra,nbt.getCompound("inner"));
			inv.deserializeNBT(ra,nbt.getCompound("inventory"));
			if(nbt.contains("lastRecipe"))
				lastRecipe=ResourceLocation.parse(nbt.getString("lastRecipe"));
			else
				lastRecipe=null;
		}

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		recipeHandler.writeCustomNBT(nbt, isClient);
		nbt.put("tank", tank.writeToNBT(ra,new CompoundTag()));
		nbt.putBoolean("inf", isInfinite);
		if (!isClient) {
			nbt.put("inventory", inv.serializeNBT(ra));
			nbt.put("inner", inner.saveOptional(ra));
			if(lastRecipe!=null)
				nbt.putString("lastRecipe", lastRecipe.toString());
		}

	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;
		boolean updateNeeded = contain.tick();
		if(!isInfinite) {
			if (!inner.isEmpty()) {
				inner = Utils.insertToOutput(inv, 5, inner);
				this.setChanged();
				return;
			}
			if(recipeHandler.shouldTestRecipe()){
				RecipeHolder<DoliumRecipe> recipe=DoliumRecipe.testDolium(tank.getFluid(), inv);
				recipeHandler.setRecipe(recipe);
				
			}
			if (recipeHandler.tickProcess(1)) {
				updateNeeded=true;
			}
		}

		if(updateNeeded)
			this.syncData();
	}

	boolean tryAddFluid(FluidStack fs) {
		if (isInfinite)
			return false;
		int tryfill = tank.fill(fs, FluidAction.SIMULATE);
		if (tryfill > 0) {
			if (tryfill == fs.getAmount()) {
				tank.fill(fs, FluidAction.EXECUTE);
				return true;
			}
			return false;
		}
		return false;
	}

	private boolean tryContianFluid() {
		ItemStack is = inv.getStackInSlot(4);
		if (!is.isEmpty() && inv.getStackInSlot(5).isEmpty()) {
			if (tank.getFluidAmount() >= 250) {
				RecipeHolder<BowlContainingRecipe> recipe = BowlContainingRecipe.getRecipes(is).stream().filter(t->t.value().matches(this.tank.getFluid())).findFirst().orElse(null);
				if (recipe != null) {
					is.shrink(1);
					inv.setStackInSlot(5, recipe.value().handle(tryAddSpice(tank.drain(250, FluidAction.EXECUTE))));
					return true;
				}
			}
			FluidStack out=Utils.extractFluid(is);
			if (!out.isEmpty()) {
				if (tryAddFluid(out)) {
					ItemStack ret = is.getCraftingRemainingItem();
					is.shrink(1);
					inv.setStackInSlot(5, ret);
				}
				return true;
			}
			FluidActionResult far = FluidUtil.tryFillContainer(is, this.tank, 1250, null, true);
			if (far.isSuccess()) {
				is.shrink(1);
				if (far.getResult() != null) {
					inv.setStackInSlot(5, far.getResult());
				}
				return true;
			}
			if (!isInfinite) {
				far = FluidUtil.tryEmptyContainer(is, this.tank, 1250, null, true);
				if (far.isSuccess()) {
					is.shrink(1);
					if (far.getResult() != null) {
						inv.setStackInSlot(5, far.getResult());
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new DoliumContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".counter_dolium.title");
	}

	RangedWrapper bowl = new RangedWrapper(inv, 3, 6) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot == 5)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 3 || slot == 4)
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}
	};
	RangedWrapper ingredient = new RangedWrapper(inv, 0, 3) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};

	IFluidHandler handler = new SyncedFluidHandler(this,new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int t) {
			if (t == 0)
				return tank.getFluid();
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int t) {
			if (t == 0)
				return tank.getCapacity();
			return 0;
		}

		@Override
		public boolean isFluidValid(int t, FluidStack stack) {
			return tank.isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!isInfinite)
				return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (isInfinite)
				return action.simulate() ? resource : tryAddSpice(resource);
			return action.simulate() ? tank.drain(resource, action) : tryAddSpice(tank.drain(resource, action));

		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (isInfinite)
				return action.simulate() ? tank.getFluid().copyWithAmount(maxDrain)
						: tryAddSpice(tank.getFluid().copyWithAmount(maxDrain));
			return action.simulate() ? tank.drain(maxDrain, action) : tryAddSpice(tank.drain(maxDrain, action));

		}

	});




	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}

	public ItemStackHandler getInv() {
		return inv;
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> cap, Direction side) {
		if (cap == Capabilities.ItemHandler.BLOCK) {
			if (side == Direction.UP)
				return ingredient;
			return this.bowl;
		}
		if (cap == Capabilities.FluidHandler.BLOCK)
			return handler;
		return null;
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

}
