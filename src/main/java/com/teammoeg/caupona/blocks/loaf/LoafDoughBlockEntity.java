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

package com.teammoeg.caupona.blocks.loaf;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.LoafHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LoafDoughBlockEntity extends CPBaseBlockEntity {
	int heatValue;
	int process;
	LazyTickWorker ltw=new LazyTickWorker(20,()->{
		if(!level.getBlockState(getBlockPos().below()).is(CPBlocks.LOAF_DOUGH)) {
			heatValue=(int) LoafHelper.getFireStrengh(level, getBlockPos());
			BlockPos crpos=getBlockPos();
			for(int i=0;i<CPConfig.COMMON.loafStacking.get();i++) {
				crpos=crpos.above();
				BlockState bs=level.getBlockState(crpos);
				if(!bs.is(CPBlocks.LOAF_DOUGH)||bs.getValue(SlabBlock.WATERLOGGED)) {
					break;
				}
				level.getBlockEntity(crpos, CPBlockEntityTypes.LOAF_DOUGH.get()).ifPresent(t->t.setHeatValue(heatValue));
			}
		}
		return false;
	});
	public void setHeatValue(int heatValue) {
		this.heatValue = heatValue;
	}

	
	public LoafDoughBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.LOAF_DOUGH.get(),pWorldPosition, pBlockState);
		ltw.enqueue();
	}

	@Override
	public void handleMessage(short type, int data) {
		
	}

	@Override
	public void readCustomNBT(CompoundTag arg0, boolean arg1, Provider arg2) {
		process=arg0.getInt("process");
	}

	@Override
	public void tick() {
		if(!level.isClientSide) {
			if(!this.getBlockState().getValue(SlabBlock.WATERLOGGED)) {
				ltw.tick();
				process+=heatValue;
				this.setChanged();
				if(process>=CPConfig.COMMON.loafCooking.get()) {
					level.setBlock(worldPosition,CPBlocks.LOAF.get().withPropertiesOf(this.getBlockState()), 3);
				}
			}else
				process=0;
		}
		
	}

	@Override
	public void writeCustomNBT(CompoundTag arg0, boolean arg1, Provider arg2) {
		arg0.putInt("process", process);
	}

}
