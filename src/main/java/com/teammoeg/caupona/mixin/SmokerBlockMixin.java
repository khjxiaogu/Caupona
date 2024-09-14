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

package com.teammoeg.caupona.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.caupona.CPTags.Blocks;
import com.teammoeg.caupona.client.CPParticles;
import com.teammoeg.caupona.util.ChimneyHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SmokerBlock.class)
public class SmokerBlockMixin {

	public SmokerBlockMixin() {
	}

	/**
	 * @param pState  
	 */
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", remap = true, ordinal = 0), method = "animateTick", remap = true, cancellable = true, require = 1, allow = 1)
	public void cp$animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo cbi) {
		BlockPos bp = ChimneyHelper.getNearestChimney(pLevel, pPos, 2);
		if (bp != null) {
			double motY = -0.3, delY = .5;
			if (!pLevel.getBlockState(bp).is(Blocks.CHIMNEY_POT)) {
				motY = pRandom.nextDouble() * .25;
				delY = 0;
			}
			pLevel.addParticle(CPParticles.SOOT.get(), bp.getX() + .5, bp.getY() + delY, bp.getZ() + .5,
					pRandom.nextDouble() * .5 - .25, motY, pRandom.nextDouble() * .5 - .25);
			cbi.cancel();
		}
	}
}
