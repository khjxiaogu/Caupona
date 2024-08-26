/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietResult;
import com.illusivesoulworks.diet.common.DietApiImpl;
import com.illusivesoulworks.diet.common.util.DietResult;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//As Diet's author didn't add such a more flexible api, I have to resort to mixin.
@Mixin(DietApiImpl.class)
public class DietApiImplMixin extends DietApi {
	
	private static void CP$getResult(Player player,ItemStack input, CallbackInfoReturnable<IDietResult> result) {
		Optional<IFoodInfo> oiso = CauponaHooks.getInfo(input);
		if(!oiso.isPresent())return;
		IFoodInfo ois=oiso.get();
		List<FloatemStack> is=ois.getStacks();
		Map<IDietGroup, Float> groups = new HashMap<>();
		float b=(float)(double)CPConfig.SERVER.benefitialMod.get();
		float h=(float)(double)CPConfig.SERVER.harmfulMod.get();
		for (FloatemStack sx : is) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(sx.getItem());
			ItemStack stack;
			if (fvr == null || fvr.getRepersent() == null)
				stack = sx.getStack();
			else
				stack = fvr.getRepersent();
			IDietResult dr = DietApiImpl.getInstance().get(player, stack);
			if (dr != DietResult.EMPTY)
				for (Entry<IDietGroup, Float> me : dr.get().entrySet())
					if(me.getKey().isBeneficial()) {
						groups.merge(me.getKey(), me.getValue()*sx.getCount()*b, Float::sum);
					}else
						groups.merge(me.getKey(), me.getValue()*sx.getCount()*h, Float::sum);
		}
		if(ois instanceof StewInfo si) {
			FluidFoodValueRecipe ffvr=FluidFoodValueRecipe.recipes.get(si.base);
			if(ffvr!=null&&ffvr.getRepersent()!=null) {
				IDietResult dr = DietApiImpl.getInstance().get(player,ffvr.getRepersent());
				if (dr != DietResult.EMPTY)
					for (Entry<IDietGroup, Float> me : dr.get().entrySet())
						if(me.getKey().isBeneficial()) {
							groups.merge(me.getKey(), me.getValue()*(si.shrinkedFluid+1)/ffvr.parts*b, Float::sum);
						}else
							groups.merge(me.getKey(), me.getValue()*(si.shrinkedFluid+1)/ffvr.parts*h, Float::sum);
			}
		}
		result.setReturnValue(new DietResult(groups));
	}
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lcom/illusivesoulworks/diet/api/type/IDietResult;", cancellable = true, remap = false)
	public void get(Player player, ItemStack input, CallbackInfoReturnable<IDietResult> result) {
		CP$getResult(player,input,result);
	}

	/**
	 * @param heal
	 * @param sat
	 */
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;IF)Lcom/illusivesoulworks/diet/api/type/IDietResult;", cancellable = true, remap = false)
	public void get(Player player, ItemStack input, int heal, float sat,
			CallbackInfoReturnable<IDietResult> result) {
		CP$getResult(player,input,result);
	}

}
