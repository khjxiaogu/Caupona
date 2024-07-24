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

package com.teammoeg.caupona.blocks.hypocaust;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class WolfStatueBlockEntity extends CPBaseBlockEntity {
	boolean isVeryHot;
	private LazyTickWorker check;
	public WolfStatueBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.WOLF_STATUE.get(), pWorldPosition, pBlockState);
		check = new LazyTickWorker(CPConfig.SERVER.wolfTick.get(),()->{
			BlockPos bp = this.getBlockPos();
			BlockState bs = this.getBlockState();
			int bheat = bs.getValue(WolfStatueBlock.HEAT);
			for (int i = 0; i < 4; i++) {
				bp = bp.below();
				if (this.getLevel().getBlockEntity(bp) instanceof BathHeatingBlockEntity bath) {
					int cheat = Math.min(bath.getHeat(), 2);
					
					
					if (cheat != bheat)
						this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs.setValue(WolfStatueBlock.HEAT, cheat));
					return true;
				}
			}
			if(bheat!=0)
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs.setValue(WolfStatueBlock.HEAT,0));
			return true;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		isVeryHot = nbt.getBoolean("very_hot");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		nbt.putBoolean("very_hot", isVeryHot);
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;

		if (level.getBlockEntity(this.getBlockPos().below()) instanceof IStove stove) {
			BlockState bs = this.getBlockState();
			int nh =stove.requestHeat();
			int bheat = bs.getValue(WolfStatueBlock.HEAT);
			boolean flag = false;
			bs = bs.setValue(WolfStatueBlock.HEAT, nh);
			if (bheat != nh) {
				flag = true;
			}
			if(!isVeryHot)
				this.setChanged();
			isVeryHot = nh > 0;
			if (isVeryHot && bs.getValue(WolfStatueBlock.WATERLOGGED)) {
				bs = bs.setValue(WolfStatueBlock.WATERLOGGED, false);
				this.level.levelEvent(1501, worldPosition, 0);
				flag = true;
			}
			if (flag)
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs);
			return;
		}
		isVeryHot = false;
		check.tick();
	}

}
