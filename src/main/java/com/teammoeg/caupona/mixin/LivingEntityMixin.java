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
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.teammoeg.caupona.CPMobEffects;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	public LivingEntityMixin() {
	}
	@ModifyVariable(at = @At("HEAD"),method="hurt",index=2,argsOnly=true,require=1)
	public float setAmount(float amount) {
		if(getThis().hasEffect(CPMobEffects.HYPERACTIVE))
			return amount*2*(getThis().getEffect(CPMobEffects.HYPERACTIVE).getAmplifier()+1);
		return amount;
	}
	private LivingEntity getThis() {
		return (LivingEntity)(Object)this;
	}
}
