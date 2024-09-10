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

package com.teammoeg.caupona.blocks.pot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.NotNull;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.BowlTypeRecipe;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.StewPendingContext;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.SyncedFluidHandler;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public class StewPotBlockEntity extends CPBaseBlockEntity implements MenuProvider, IInfinitable {
	private ItemStackHandler inv = new ItemStackHandler(12) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 9)
				return (stack.getItem() == Items.POTION&&!StreamSupport.stream(stack.get(DataComponents.POTION_CONTENTS).getAllEffects().spliterator(),false).anyMatch(t->t.getDuration()==1)) || StewCookingRecipe.isCookable(stack);
			if (slot == 9) {
				return stack.is(Items.BOWL) || stack.is(CPBlocks.LOAF_BOWL.get().asItem()) || Utils.getFluidType(stack)!=Fluids.EMPTY || AspicMeltingRecipe.find(stack) != null;
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

	public ItemStackHandler getInv() {
		return inv;
	}

	private FluidTank tank = new FluidTank(1250, StewCookingRecipe::isBoilable) {
		protected void onContentsChanged() {
			resetStillCounter();
		}

	};

	public StewPotBlockEntity(BlockPos p, BlockState s) {
		super(CPBlockEntityTypes.STEW_POT.get(), p, s);
		still=new LazyTickWorker(()->{
			if (inv.getStackInSlot(10).isEmpty()) {
				DoliumRecipe recipe = DoliumRecipe.testPot(getTank().getFluid());
				if (recipe != null) {
					ItemStack out = recipe.handle(getTank().getFluid());
					
					inv.setStackInSlot(10, out);
				}
				
			}
			resetStillCounter();
			return true;
		});
		contain=new LazyTickWorker(CPConfig.SERVER.containerTick.get(),()->{
			if (isInfinite) {
				FluidStack fs = tank.getFluid().copyWithAmount(tank.getFluidAmount());
				if (canAddFluid())
					tryContianFluid();
				tank.setFluid(fs);
			} else {
				if (canAddFluid()) {
					if(tryContianFluid())
						return true;
				}
			}
			return false;
		});
	}

	public FluidTank getTank() {
		return tank;
	}

	//Process
	public int process;
	public int processMax;

	public LazyTickWorker still;

	public LazyTickWorker contain;

	//stores working properties
	public boolean working = false;
	public boolean operate = false;
	public short proctype = 0;
	public boolean rsstate = false;
	boolean mayBeStill=true;
	
	boolean isInfinite = false;

	//stores Result
	public FluidStack output;
	public void resetResult() {
		output=null;
	}
	public static final short NOP = 0;
	public static final short BOILING = 1;
	public static final short COOKING = 2;
	public static final short STIRING = 3;
	public void resetStillCounter() {
		mayBeStill=true;
		still.stop();
	}
	@Override
	public void tick() {
		boolean syncNeeded=false;
		if (!level.isClientSide) {
			working = false;
			if (processMax > 0) {
				resetStillCounter();
				if (level.getBlockEntity(worldPosition.below()) instanceof IStove stove) {
					int rh = stove.requestHeat();
					if (!isInfinite) {
						process += rh;
						if(rh>0)
							syncNeeded=true;
					}
					if (rh > 0)
						working = true;
					if (process >= processMax) {
						process = 0;
						processMax = 0;
						doWork();
						syncNeeded=true;
					}
				} else
					return;

			} else {
				if (!tank.isEmpty() && !isInfinite) {
					syncNeeded|=still.tick();
					if(!still.isRunning()&&mayBeStill) {
						DoliumRecipe rcp=DoliumRecipe.testPot(tank.getFluid());
						if(rcp!=null) {
							still.start(rcp.time);
						}else mayBeStill=false;
					}
				}
				if (!isInfinite&&proctype<=1) {
					prepareWork();
					if(proctype!=0)
						syncNeeded=true;
				}
				syncNeeded|=contain.tick();
			}
			if(syncNeeded)
				this.syncData();
		}
		
		
	}
	
	private FluidStack tryAddSpice(FluidStack fs) {
		SpiceRecipe spice = null;
		ItemStack ospi = inv.getStackInSlot(11);
		ItemStack spi=ospi;
		if (fs.getAmount() % 250 == 0 && fs.getFluid() instanceof SoupFluid)
			spice = SpiceRecipe.find(spi);
		StewInfo si = null;
		
		if(this.getBlockState().is(CPBlocks.STEW_POT_LEAD.get())) {
			if(spice==null) {
				si=Utils.getOrCreateInfo(fs);
				if(si.getDensity()>1.5f) {
					spi=CPItems.getSapa();
					spice=SpiceRecipe.find(spi);
				}
			}else if(spice.canReactLead) {
				spi=CPItems.getSapa();
				spice=SpiceRecipe.find(spi);
			}
		}
		if (spice != null) {
			if(si ==null)
				si=Utils.getOrCreateInfo(fs);
			if (!si.canAddSpice())
				return fs;
			if (!isInfinite) {
				int consume = fs.getAmount() / 250;
				if (SpiceRecipe.getMaxUse(spi) < consume)
					return fs;
				inv.setStackInSlot(11, SpiceRecipe.handle(ospi, consume));
			}
			si.addSpice(spice.effect, spi);

			Utils.setInfo(fs, si);
		}
		return fs;
	}
	private boolean tryContianFluid() {
		ItemStack is = inv.getStackInSlot(9);
		if (!is.isEmpty() && inv.getStackInSlot(10).isEmpty()) {
			if (tank.getFluidAmount() >= 250) {
				for(RecipeHolder<BowlTypeRecipe> type:BowlTypeRecipe.recipes) {
					if(type.value().test(is)) {
						RecipeHolder<BowlContainingRecipe> recipe = BowlContainingRecipe.getRecipes(type.value().bowl).stream().filter(t->t.value().matches(this.tank.getFluid())).findFirst().orElse(null);
						if (recipe != null) {
							is.shrink(1);
							inv.setStackInSlot(5, recipe.value().handle(tryAddSpice(tank.drain(250, FluidAction.EXECUTE))));
							return true;
						}
					}
				}
			}
			FluidStack out=Utils.extractFluid(is);
			if (!out.isEmpty()) {
				if (tryAddFluid(out)) {
					ItemStack ret = is.getCraftingRemainingItem();
					is.shrink(1);
					inv.setStackInSlot(10, ret);
					return true;
				}
				return false;
			}
			if (!isInfinite) {
				AspicMeltingRecipe amr = AspicMeltingRecipe.find(is);
				if (amr != null) {
					int remainSpace = tank.getCapacity() - tank.getFluidAmount();
					int produce = Math.min(remainSpace / amr.amount, is.getCount());
					FluidStack fs = amr.handle(is);
					fs.setAmount(fs.getAmount() * produce);
					if (tryAddFluid(fs, amr.time, false)) {
						ItemStack ret = is.getCraftingRemainingItem();
						ret.setCount(produce);
						is.shrink(produce);
						still.stop();
						inv.setStackInSlot(10, ret);
						return true;
					}
					return false;
				}
			}
			FluidActionResult far = FluidUtil.tryFillContainer(is, this.tank, 1250, null, true);
			if (far.isSuccess()) {
				is.shrink(1);
				if (far.getResult() != null) {
					still.stop();
					inv.setStackInSlot(10, far.getResult());
				}
			}
		}
		return false;
	}

	public boolean canAddFluid() {
		return proctype == 0;
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider provider) {
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		proctype = nbt.getShort("worktype");
		rsstate = nbt.getBoolean("rsstate");
		if (inv.getSlots() < 12)
			inv.setSize(12);
		if (isClient)
			working = nbt.getBoolean("working");
		tank.readFromNBT(provider,nbt);
		if(nbt.contains("output"))
			output=FluidStack.parse(provider, nbt.get("output")).orElse(null);
		isInfinite = nbt.getBoolean("inf");
		if (!isClient) {
			inv.deserializeNBT(provider,nbt.getCompound("inv"));
			still.read(nbt,"nowork");
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider provider) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("worktype", proctype);
		nbt.putBoolean("rsstate", rsstate);
		if (isClient)
			nbt.putBoolean("working", working);

		tank.writeToNBT(provider, nbt);
		if(output!=null)
			nbt.put("output", output.save(provider));
		
		nbt.putBoolean("inf", isInfinite);
		if (!isClient) {
			nbt.put("inv", inv.serializeNBT(provider));
			still.write(nbt,"nowork");
		}
	}

	private void prepareWork() {
		if (rsstate&&proctype==0&& !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate&&proctype==0) {
			operate = false;
			if (!(level.getBlockEntity(worldPosition.below()) instanceof IStove stove) || !stove.canEmitHeat())
				return;
			if (doBoil())
				proctype = 1;
			else if (makeSoup())
				proctype = 2;
		} else if (proctype == 1) {
			if (makeSoup())
				proctype = 2;
			else {
				proctype = 0;
				this.syncData();
			}
		}
	}

	private void doWork() {
		if(output!=null) {
			this.tank.setFluid(output);
			output=null;
			if (proctype == 1) {
				boolean hasItem = false;
				for (int i = 0; i < 9; i++) {
					ItemStack is = inv.getStackInSlot(i);
					if (!is.isEmpty()) {
						hasItem = true;
						break;
					}
				}
				if (!hasItem)
					proctype = 0;
			}else proctype = 0;
		}else proctype = 0;
	}

	private boolean doBoil() {
		RecipeHolder<BoilingRecipe> recipeh = BoilingRecipe.recipes.stream().filter(t->t.value().matches(this.tank.getFluid())).findFirst().orElse(null);
		if (recipeh == null)
			return false;
		BoilingRecipe recipe=recipeh.value();
		output=recipe.handle(this.tank.getFluid());
		this.processMax = (int) (recipe.time * (this.tank.getFluidAmount() / 250f));
		this.process = 0;
		
		return true;
	}

	private void adjustParts(int count) {
		float oparts = tank.getFluidAmount() / 250f;
		int parts = (int) (oparts + count);
		StewInfo currentInfo=Utils.getOrCreateInfo(tank.getFluid()).copy();
		currentInfo.adjustParts(oparts, parts);
		tank.getFluid().setAmount(parts * 250);
		Utils.setInfo(tank.getFluid(), currentInfo);
	}

	private boolean makeSoup() {
		//System.out.println("1");
		if (tank.getFluidAmount() <= 250)
			return false;// can't boil if under one bowl
		StewInfo currentInfo=Utils.getOrCreateInfoForRead(tank.getFluid());
		//System.out.println("2");
		if (currentInfo.getStacks().size() > 27)
			return false;// too much ingredients
		int oparts = tank.getFluidAmount() / 250;
		int parts = oparts - 1;
		int itms = 0;
		List<MobEffectInstance> cr = new ArrayList<>(currentInfo.getPotionEffects());
		//System.out.println("3");
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {

				if (is.getItem() == Items.POTION) {
					outer: for (MobEffectInstance n : is.get(DataComponents.POTION_CONTENTS).getAllEffects()) {
						for (MobEffectInstance eff : cr) {
							if (StewInfo.isEffectEquals(eff, n))
								continue outer;
						}
						cr.add(n);
					}
				} else if (StewCookingRecipe.isCookable(is))
					itms++;
				else
					return false;
			}
		}
		//System.out.println("4:"+itms+"/"+parts+"/"+cr.size()+"/"+currentInfo.getDensity());
		if (itms / (float) parts + (currentInfo.getDensity() * oparts) / parts > 3 || cr.size() > 3) {// too dense
			return false;
		}
		//System.out.println("5");
		process = 0;
		adjustParts(-1);
		currentInfo=Utils.getOrCreateInfo(tank.getFluid());
		boolean hasItem = false;
		NonNullList<ItemStack> interninv = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				if (is.getItem() == Items.POTION) {
					for (MobEffectInstance eff : is.get(DataComponents.POTION_CONTENTS).getAllEffects())
						currentInfo.addEffect(eff, parts);
					inv.setStackInSlot(i, new ItemStack(Items.GLASS_BOTTLE));
				} else {
					for (int j = 0; j < 9; j++) {
						ItemStack ois = interninv.get(j);
						if (ois.isEmpty()) {
							interninv.set(j, is.copy());
							break;
						} else if (ItemStack.isSameItemSameComponents(ois, is)) {
							ois.setCount(ois.getCount() + is.getCount());
							break;
						}
					}
					inv.setStackInSlot(i, is.getCraftingRemainingItem());
				}
				hasItem = true;
			}
		}
		//System.out.println("6");
		if (!hasItem) {// just reduce water
			currentInfo.completeEffects();
			processMax = Math.max(CPConfig.SERVER.potCookTimeBase.get(), decideSoup());
			return true;
		}
		//System.out.println("7");
		int tpt = CPConfig.SERVER.potMixTimeBase.get();
		outer: for (int i = 0; i < 9; i++) {
			ItemStack is = interninv.get(i);
			if (is.isEmpty())
				break;
			currentInfo.addItem(is, parts);
			for (RecipeHolder<DissolveRecipe> rs : DissolveRecipe.recipes) {
				if (rs.value().item.test(is)) {
					tpt += rs.value().time;
					continue outer;
				}
			}
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
			if (fvr != null)
				tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
		}
		currentInfo.completeAll();
		tpt = Math.max(CPConfig.SERVER.potCookTimeBase.get(), tpt);
		interninv.clear();
		output=tank.getFluid().copy();
		
		Utils.setInfo(output, currentInfo);
		decideSoup();
		processMax = Math.max(decideSoup(), tpt);
		
		return true;
	}
	
	private int decideSoup() {
		if(output==null)
			output=tank.getFluid();
		Fluid become = output.getFluid();
		StewInfo currentInfo=Utils.getOrCreateInfo(output);
		StewPendingContext ctx = new StewPendingContext(currentInfo, become);
		Fluid nextbase = become;
		if (ctx.getItems().isEmpty()) {
			return 0;
		}
		
		for (RecipeHolder<StewCookingRecipe> cr : StewCookingRecipe.sorted) {
			int mt = cr.value().matches(ctx);
			//System.out.println(cr.id()+":"+mt);
			if (mt != 0) {
				if (mt == 2)
					nextbase = become;
				else
					nextbase = currentInfo.getBase();
				become = cr.value().output;
				FluidStack preout=new FluidStack(become,output.getAmount());
				if(!cr.value().removeNBT) {
					currentInfo.setBase(nextbase);
					preout.applyComponents(output.getComponentsPatch());
					currentInfo.recalculateHAS();
					Utils.setInfo(preout, currentInfo);
				}
				output=preout;
				
				return cr.value().time;
			}
		}

		return 0;
	}


	public boolean canAddFluid(FluidStack fs) {
		if (isInfinite)
			return false;
		int tryfill = tank.fill(fs, FluidAction.SIMULATE);
		if (tryfill > 0) {
			if (tryfill == fs.getAmount()) {
				return true;
			}
			return false;
		}
		if (tank.getCapacity() - tank.getFluidAmount() < fs.getAmount())
			return false;
		if (!(level.getBlockEntity(worldPosition.below()) instanceof IStove stove) || !stove.canEmitHeat())
			return false;
		StewInfo n = Utils.getOrCreateInfoForRead(fs);
		StewInfo currentInfo=Utils.getOrCreateInfoForRead(tank.getFluid());
		if ((currentInfo.getBase()!=n.getBase())
				&& (n.getBase()!=tank.getFluid().getFluid())) {
			RecipeHolder<BoilingRecipe> bnx = BoilingRecipe.recipes.stream().filter(t->t.value().matches(fs)).findFirst().orElse(null);
			if (bnx == null)
				return false;
			if (currentInfo.getBase()!=bnx.value().after)
				return false;
		}
		return currentInfo.canMerge(n, tank.getFluidAmount() / 250f, fs.getAmount() / 250f);
	}

	public boolean tryAddFluid(FluidStack fs) {
		return tryAddFluid(fs, CPConfig.SERVER.potMixTimeBase.get(), true);
	}

	public boolean tryAddFluid(FluidStack fs, int extraTime, boolean canIgnoreHeat) {
		if (isInfinite)
			return false;
		if (canIgnoreHeat) {
			int tryfill = tank.fill(fs, FluidAction.SIMULATE);
			if (tryfill > 0) {
				if (tryfill == fs.getAmount()) {
					tank.fill(fs, FluidAction.EXECUTE);
					return true;
				}
				return false;
			}
		} else if (tank.isEmpty()) {
			int tryfill = tank.fill(fs, FluidAction.SIMULATE);
			if (tryfill > 0) {
				if (tryfill == fs.getAmount()) {
					tank.fill(fs, FluidAction.EXECUTE);
					this.proctype = 3;
					this.process = 0;
					this.processMax = extraTime;
					resetResult();
					return true;
				}
				return false;
			}
		}
		if (tank.getCapacity() - tank.getFluidAmount() < fs.getAmount())
			return false;
		if (!(level.getBlockEntity(worldPosition.below()) instanceof IStove stove) || !stove.canEmitHeat())
			return false;
		StewInfo n = Utils.getOrCreateInfo(fs);
		int pm = 0;
		StewInfo currentInfo=Utils.getOrCreateInfo(tank.getFluid());
		if (currentInfo.getBase()!=n.getBase() && currentInfo.getBase()!=fs.getFluid()
				&& n.getBase()!=tank.getFluid().getFluid()) {
			FluidStack fst=fs;
			RecipeHolder<BoilingRecipe> bnx = BoilingRecipe.recipes.stream().filter(t->t.value().matches(fst)).findFirst().orElse(null);
			if (bnx == null)
				return false;
			if (currentInfo.getBase()!=bnx.value().after)
				return false;
			fs = bnx.value().handle(fs);
			pm = (int) (bnx.value().time * (fs.getAmount() / 250f));
		}

		if (currentInfo.merge(n, tank.getFluidAmount() / 250f, fs.getAmount() / 250f)) {
			this.adjustParts(fs.getAmount() / 250);
			int num = Math.max(decideSoup(), extraTime);
			this.proctype = 3;
			this.process = 0;
			this.processMax = Math.max(pm, num);
			resetStillCounter();
			return true;
		}

		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new StewPotContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".stewpot.title");
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			if (this.proctype == 0)
				this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}
		this.syncData();

	}

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
			if (t == 0 && canAddFluid())
				return tank.isFluidValid(stack);
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (canAddFluid() && !isInfinite)
				return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {

			if (canAddFluid()) {
				if (isInfinite)
					return action.simulate() ? resource : tryAddSpice(resource);
				return action.simulate() ? tank.drain(resource, action) : tryAddSpice(tank.drain(resource, action));
			}
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {

			if (canAddFluid()) {
				if (isInfinite)
					return action.simulate() ? tank.getFluid().copyWithAmount(maxDrain)
							: tryAddSpice(tank.getFluid().copyWithAmount(maxDrain));
				return action.simulate() ? tank.drain(maxDrain, action) : tryAddSpice(tank.drain(maxDrain, action));
			}
			return FluidStack.EMPTY;
		}

	});
	public IItemHandler bowl = new IItemHandler() {

		@Override
		public int getSlots() {
			return inv.getSlots();
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return inv.getStackInSlot(slot);
		}

		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			if(slot<9||slot == 10)
				return stack;
			return inv.insertItem(slot, stack, simulate);
		}

		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			if(slot==9||slot==11)
				return ItemStack.EMPTY;
			if(slot<9&&inv.isItemValid(slot,inv.getStackInSlot(slot))){
				return ItemStack.EMPTY;
			}
			return inv.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return inv.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if(slot<9||slot == 10)
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
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}

}
