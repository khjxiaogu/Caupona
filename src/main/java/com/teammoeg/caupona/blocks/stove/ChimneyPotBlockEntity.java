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

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ChimneyPotBlockEntity extends CPBaseBlockEntity {
	private int process;
	private int processMax;
	int countSoot;
	private int maxStore;

	public ChimneyPotBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.CHIMNEY_POT.get(), pWorldPosition, pBlockState);
		processMax = CPConfig.SERVER.chimneyTicks.get();
		maxStore = CPConfig.SERVER.chimneyStorage.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		process = nbt.getInt("process");
		countSoot = nbt.getInt("soot");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		nbt.putInt("process", process);
		nbt.putInt("soot", countSoot);
	}

	public void addAsh(int val) {
		process += val;
		this.setChanged();
	}

	@Override
	public void tick() {
		if(!level.isClientSide)
			if (process >= processMax) {
				if (countSoot < maxStore) {
					countSoot++;
					this.syncData();
				}
				process = 0;
				this.setChanged();
			}
	}

}
