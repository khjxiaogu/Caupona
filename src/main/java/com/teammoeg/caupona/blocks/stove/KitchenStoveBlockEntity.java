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

package com.teammoeg.caupona.blocks.stove;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.CPTags.Blocks;
import com.teammoeg.caupona.client.CPParticles;
import com.teammoeg.caupona.client.util.DisplayGroupProperty;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.ChimneyHelper;
import com.teammoeg.caupona.util.FuelType;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class KitchenStoveBlockEntity extends CPBaseBlockEntity implements Container, MenuProvider, IStove, IInfinitable {
	private NonNullList<ItemStack> fuel = NonNullList.withSize(1, ItemStack.EMPTY);
	public int process;
	public int processMax;
	private final int speed;
	private final int maxcd;
	private int cd;
	private float fuelMod = 1f;
	public BlockPos attachedChimney;
	private int chimneyTicks = 0;
	private int chimneyCheckTicks = 20;
	boolean isInfinite = false;
	public FuelType inventory_fuel = FuelType.OTHER;
	public FuelType current=FuelType.OTHER;

	public KitchenStoveBlockEntity(BlockEntityType<KitchenStoveBlockEntity> tet, BlockPos p, BlockState s, int spd) {
		super(tet, p, s);
		this.speed = spd;
		maxcd = CPConfig.SERVER.stoveCD.get() / speed;
		fuelMod = (float)(double)CPConfig.SERVER.stoveFuel.get();
		chimneyCheckTicks = CPConfig.SERVER.chimneyCheck.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		if (nbt.contains("chimneyPos"))
			attachedChimney = BlockPos.of(nbt.getLong("chimneyPos"));
		else
			attachedChimney = null;
		inventory_fuel = FuelType.parse(nbt.getString("fuel_type"));
		current = FuelType.parse(nbt.getString("current_fuel"));
		if (!isClient) {
			cd = nbt.getInt("cd");
			fuel.set(0, ItemStack.parseOptional(registries,nbt.getCompound("fuel")));
			chimneyTicks = nbt.getInt("chimneyTick");
			isInfinite = nbt.getBoolean("inf");
		}
		refreshModel();
	}
	public void refreshModel() {
		if(this.getLevel()!=null&&this.getLevel().isClientSide) {
			getLevel().getModelDataManager().requestRefresh(this);
			getLevel().sendBlockUpdated(this.getBlockPos(), getBlockState(),getBlockState(),3);
		}
	}
	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		if (attachedChimney != null)
			nbt.putLong("chimneyPos", attachedChimney.asLong());
		nbt.putString("fuel_type", inventory_fuel.serialize());
		nbt.putString("current_fuel", current.serialize());
		if (!isClient) {
			nbt.putInt("cd", cd);
			nbt.put("fuel", fuel.get(0).saveOptional(registries));
			nbt.putInt("chimneyTick", chimneyTicks);
			nbt.putBoolean("inf", isInfinite);
		}
	}

	@Override
	public ModelData getModelData() {
		if(inventory_fuel==FuelType.OTHER&&current==FuelType.OTHER) {//no data
			return ModelData.builder().with(DisplayGroupProperty.PROPERTY, ImmutableSet.of()).build();
		}
		String ash=this.getBlockState().getValue(BlockStateProperties.LIT)?current.hot_ash():current.cold_ash();
		String model=inventory_fuel.modelLayer();
		return ModelData.builder().with(DisplayGroupProperty.PROPERTY, ash==null?(model!=null?ImmutableSet.of(model):ImmutableSet.of()):(model!=null?ImmutableSet.of(model,ash):ImmutableSet.of(ash))).build();
	}

	@Override
	public void clearContent() {
		fuel.clear();
		inventory_fuel=FuelType.OTHER;
		refreshModel();
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return fuel.get(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return fuel.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack removed=ContainerHelper.removeItem(fuel, index, count);
		inventory_fuel=FuelType.getType(fuel.get(0));
		this.setChanged();
		refreshModel();
		
		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		inventory_fuel=FuelType.OTHER;
		this.setChanged();
		refreshModel();
		
		return ContainerHelper.takeItem(fuel, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.fuel.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		inventory_fuel=FuelType.getType(fuel.get(0));
		this.setChanged();
		refreshModel();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return stack.getBurnTime(RecipeType.SMELTING) > 0 ;
	}

	@Override
	public AbstractContainerMenu createMenu(int a, Inventory b, Player c) {
		return new KitchenStoveContainer(a, b, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".kitchen_stove.title");
	}

	private boolean consumeFuel() {
		int time = fuel.get(0).getBurnTime(RecipeType.SMELTING);
		if (time <= 0) {
			process = processMax = 0;
			current=FuelType.OTHER;
			return false;
		}
		current = FuelType.getType(fuel.get(0));
		ItemStack remain=fuel.get(0).getCraftingRemainingItem();
		fuel.get(0).shrink(1);
		if(fuel.get(0).isEmpty()) {
			fuel.set(0, remain);
		}else {
			Utils.dropToWorld(level, remain, getBlockPos());
		}
		inventory_fuel=FuelType.getType(fuel.get(0));
		float ftime = time * fuelMod / speed;
		float frac = Mth.frac(ftime);
		if (frac > 0)
			processMax = process = (int) ftime + (this.level.random.nextDouble() < frac ? 1 : 0);
		else
			processMax = process = (int) ftime;
		return true;
	}
	boolean syncNeeded=false;
	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (!level.isClientSide) {// server logic
			BlockState bs = this.getBlockState();
			
			chimneyTicks++;
			if (chimneyTicks >= chimneyCheckTicks) {
				chimneyTicks = 0;
				BlockPos newChimney=ChimneyHelper.getNearestChimney(this.getLevel(), this.getBlockPos(), 2);
				
				if(!Objects.equals(newChimney, attachedChimney)) {
					
					attachedChimney = newChimney;
					syncNeeded=true;
				}
			}
			boolean flag = false;
			if (process <= 0 && (bs.getValue(KitchenStove.LIT))) {
				bs = bs.setValue(KitchenStove.LIT, false);
				flag = true;
			}
			if (process > 0) {
				if (bs.getValue(KitchenStove.LIT)) {
					cd--;
					if (!isInfinite) {
						process--;
						
					}
					if (attachedChimney != null) {
						if (this.getLevel().getBlockEntity(attachedChimney) instanceof ChimneyPotBlockEntity chimney) {
							chimney.addAsh(speed);
						}
					}
					if (cd <= 0) {
						bs = bs.setValue(KitchenStove.LIT, false);
						flag = true;
					}
					this.setChanged();
				}
			}
			if (flag)
				this.level.setBlockAndUpdate(this.getBlockPos(), bs);
			if(syncNeeded) {
				syncNeeded=false;
				this.syncData();
			}
		} else {// client particles
			if (this.getBlockState().getValue(KitchenStove.LIT)) {
				double d0 = this.getBlockPos().getX();
				double d1 = this.getBlockPos().getY();
				double d2 = this.getBlockPos().getZ();
				RandomSource rand = this.getLevel().random;
				if (attachedChimney == null) {
					if (rand.nextDouble() < 0.25D * speed) {
						this.getLevel().addParticle(ParticleTypes.SMOKE, d0 + .5, d1 + 1, d2 + .5,
								rand.nextDouble() * .5 - .25, rand.nextDouble() * .125, rand.nextDouble() * .5 - .25);
					}
				} else {
					if (rand.nextDouble() < 0.25D * speed) {
						double motY = -0.3, delY = .5;
						if (!this.getLevel().getBlockState(attachedChimney).is(Blocks.CHIMNEY_POT)) {
							motY = rand.nextDouble() * .25;
							delY = 0;
						}
						this.getLevel().addParticle(CPParticles.SOOT.get(), attachedChimney.getX() + .5,
								attachedChimney.getY() + delY, attachedChimney.getZ() + .5,
								rand.nextDouble() * .5 - .25, motY, rand.nextDouble() * .5 - .25);
					}
				}
			}
		}
	}

	@Override
	public int requestHeat() {
		if (this.process <= 0) {
			if (!consumeFuel()) {
				return 0;
			}
			syncNeeded=true;
			this.setChanged();
			if (!isInfinite)
				process--;
		}
		BlockState bs = this.getBlockState();
		cd = maxcd;
		if (!bs.getValue(KitchenStove.LIT))
			this.level.setBlockAndUpdate(this.getBlockPos(), bs.setValue(KitchenStove.LIT, true));

		return speed;
	}

	@Override
	public boolean canEmitHeat() {
		return this.process > 0 || fuel.get(0).getBurnTime(RecipeType.SMELTING) > 0;
	}

	public int getSpeed() {
		return speed;
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}

}
