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

package com.teammoeg.caupona.blocks.hypocaust;

import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BathHeatingBlockEntity extends CPBaseBlockEntity {
	private double rate;
	private int val;
	protected LazyTickWorker process;
	protected int heat;//
	private boolean water;
	public BathHeatingBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		rate = CPConfig.SERVER.bathChance.get();
		val = CPConfig.SERVER.bathExp.get();
		process = new LazyTickWorker(CPConfig.SERVER.bathPath.get(),()->{
			if(this.heat!=0) {
				this.heat = 0;
				return true;
			}
			return false;
		});
		water=!CPConfig.SERVER.strictWater.get();
	}

	public int getHeat() {
		return heat;
	};

	public void setHeat(int val) {
		if (heat > val)
			return;
		process.rewind();
		if(heat!=val) {
			heat = val;
			this.syncData();
		}else this.setChanged();
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		heat = nbt.getInt("bathHeat");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		nbt.putInt("bathHeat", heat);
	}

	protected static boolean inRange(int pos, double d) {
		return d >= pos && d < pos + 1;
	}
	protected static boolean inRange(double pos, double d,double dmax) {
		return d >= pos && d < pos + dmax;
	}
	protected boolean isInWater(Player p) {
		if(water)return true;
		if(p.isInWaterOrBubble())return true;
		Entity e=p.getVehicle();
		while(e!=null) {
			if(e.isInWaterOrBubble())return true;
			e=p.getVehicle();
		}
		return false;
	}
	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if(level.isClientSide)return;
		int heat = getHeat();
		if (val > 0 && heat > 0 && this.level.random.nextDouble() < rate
				&& this.getLevel().getFluidState(this.getBlockPos().above()).is(FluidTags.WATER)) {
			int posX = this.getBlockPos().getX();
			int posY = this.getBlockPos().getY();
			int posZ = this.getBlockPos().getZ();
			int addExp = val * heat;
			for (Player p : this.getLevel().players()) {
				if (inRange(posX, p.getX()) && inRange(posZ, p.getZ()) && inRange(posY, p.getY(),2)&&isInWater(p)) {
				
					p.giveExperiencePoints(addExp);
				}
			}
		}
		if(process.tick()) {
			this.syncData();
		}
	}

}
