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

package com.teammoeg.caupona.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class DynamicRenderLevel implements BlockAndTintGetter {
	public DynamicRenderLevel(Level lvl, BlockPos center, Axis rotationAxis) {
		super();
		this.lvl = lvl;
		this.center = center;
		this.rotationAxis = rotationAxis;
	}

	Level lvl;
	BlockPos center;
	Axis rotationAxis;
	public BlockPos getDelta() {
		return center.above();	
	}
	@Override
	public BlockEntity getBlockEntity(BlockPos pPos) {
		// TODO Auto-generated method stub
		Vec3i dpos=pPos.subtract(center);
		if(Math.abs(dpos.getX())<=1&&Math.abs(dpos.getY())<=1&&Math.abs(dpos.getZ())<=1) {
			Direction dir=Direction.fromDelta(dpos.getX(), dpos.getY(), dpos.getZ());
			if(dir.getAxis()!=rotationAxis) {
				return lvl.getBlockEntity(getDelta());
			}
		}
		return lvl.getBlockEntity(pPos);
	}

	@Override
	public BlockState getBlockState(BlockPos pPos) {
		// TODO Auto-generated method stub
		Vec3i dpos=pPos.subtract(center);
		if(Math.abs(dpos.getX())<=1&&Math.abs(dpos.getY())<=1&&Math.abs(dpos.getZ())<=1) {
			Direction dir=Direction.fromDelta(dpos.getX(), dpos.getY(), dpos.getZ());
			if(dir.getAxis()!=rotationAxis) {
				return lvl.getBlockState(getDelta());
			}
		}
		return lvl.getBlockState(pPos);
	}

	@Override
	public FluidState getFluidState(BlockPos pPos) {
		// TODO Auto-generated method stub
		Vec3i dpos=pPos.subtract(center);
		if(Math.abs(dpos.getX())<=1&&Math.abs(dpos.getY())<=1&&Math.abs(dpos.getZ())<=1) {
			Direction dir=Direction.fromDelta(dpos.getX(), dpos.getY(), dpos.getZ());
			if(dir.getAxis()!=rotationAxis) {
				return lvl.getFluidState(getDelta());
			}
		}
		return lvl.getFluidState(pPos);
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return lvl.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		// TODO Auto-generated method stub
		return lvl.getMinBuildHeight();
	}

	@Override
	public float getShade(Direction pDirection, boolean pShade) {
		
		// TODO Auto-generated method stub
		return lvl.getShade(pDirection, pShade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		// TODO Auto-generated method stub
		return lvl.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver) {
		// TODO Auto-generated method stub
		Vec3i dpos=pBlockPos.subtract(center);
		if(Math.abs(dpos.getX())<=1&&Math.abs(dpos.getY())<=1&&Math.abs(dpos.getZ())<=1) {
			Direction dir=Direction.fromDelta(dpos.getX(), dpos.getY(), dpos.getZ());
			if(dir.getAxis()!=rotationAxis) {
				return lvl.getBlockTint(getDelta(), pColorResolver);
			}
		}
		return lvl.getBlockTint(pBlockPos, pColorResolver);
	}

}
