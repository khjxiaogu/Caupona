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

package com.teammoeg.caupona.blocks.fumarole;

import java.util.Iterator;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPTags.Blocks;
import com.teammoeg.caupona.CPTags.Fluids;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FumaroleVentBlockEntity extends CPBaseBlockEntity implements IStove {
	private final int heat;
	LazyTickWorker update;
	LazyTickWorker check;
	public FumaroleVentBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.FUMAROLE.get(), pWorldPosition, pBlockState);
		heat = CPConfig.SERVER.fumarolePower.get();
		check = new LazyTickWorker(CPConfig.SERVER.fumaroleCheck.get(),()->{
			BlockState bs = this.getBlockState();
			BlockState below = this.getLevel().getBlockState(this.getBlockPos().below(2));
			int cheat = bs.getValue(FumaroleVentBlock.HEAT);
			if (below.is(Blocks.FUMAROLE_VERY_HOT_BLOCK)) {
				if (cheat != 2) {
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 2));
				}
			} else if (below.is(Blocks.FUMAROLE_HOT_BLOCK)) {
				if (cheat != 1) {
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 1));
				}
			} else if (cheat != 0) {
				this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 0));
			}
			return true;
		});
		update = new LazyTickWorker(CPConfig.SERVER.fumaroleSpeed.get(),()->{
			if (!this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
				placeFumarole(this.getLevel(), this.getBlockPos());
			return true;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		if (isClient)
			return;
		update.read(nbt,"update");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries) {
		if (isClient)
			return;
		update.write(nbt,"update");
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		BlockState bs = this.getBlockState();
		if (bs.getValue(FumaroleVentBlock.HEAT) == 2) {
			update.tick();
			this.setChanged();
		}
		check.tick();
		
	}

	public static void placeFumarole(Level pLevel, BlockPos pPos) {
		RandomSource pRandom = pLevel.getRandom();
		int dx = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(6));
		int dz = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(6));
		if (dx == 0 && dz == 0)
			return;

		BlockPos pendPos = pPos.offset(dx, 0, dz);
		for (int i = 0; i < 3; i++) {
			BlockState b0 = pLevel.getBlockState(pendPos);
			BlockState b1 = pLevel.getBlockState(pendPos.below());
			if (b0.isAir()) {
				if (b1.getFluidState().is(Fluids.PUMICE_ON)) {
					if (shouldPlacePumice(pLevel, pendPos))
						pLevel.setBlockAndUpdate(pendPos, CPBlocks.PUMICE_BLOOM.get().defaultBlockState());
					return;
				}
			}
			pendPos = pendPos.below();
		}
	}

	public static boolean shouldPlacePumice(Level pLevel, BlockPos pPos) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return false;
		int cnt = 0;
		AABB aabb = new AABB(pPos.getX()-1,pPos.getY(),pPos.getZ()-1, pPos.getX()+1,pPos.getY(),pPos.getZ()+1);
		Iterator<BlockState> it = pLevel.getBlockStates(aabb).iterator();
		while (it.hasNext()) {
			if (it.next().getBlock() == CPBlocks.PUMICE_BLOOM.get())
				cnt++;
			if (cnt >= 2)
				return false;
		}
		return true;
	}

	@Override
	public int requestHeat() {
		if (this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
			return 0;
		return heat;
	}

	@Override
	public boolean canEmitHeat() {
		if (heat == 0)
			return false;
		if (this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
			return false;
		return this.getBlockState().getValue(FumaroleVentBlock.HEAT) != 0;
	}

}
