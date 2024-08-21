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

package com.teammoeg.caupona.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DishItem extends EdibleBlock {
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).usingConvertsTo(Items.BOWL)
			.build();
	public final DishBlock bl;

	public DishItem(DishBlock block, Properties props) {
		super(block, props.food(fakefood));
		CPItems.dish.add(this);
		bl = block;
	}



	@Override
	public int getUseDuration(ItemStack stack,LivingEntity ent) {
		return 32;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		IFoodInfo iinfo = CPCapability.FOOD_INFO.getCapability(stack, null);
		if(iinfo instanceof SauteedFoodInfo info) {
			FloatemStack fs = info.stacks.stream()
					.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
					.orElse(null);
			if (fs != null)
				tooltip.add(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
			ResourceLocation rl = info.spiceName;
			if (rl != null)
				tooltip.add(Utils.translate("tooltip.caupona.spice",
						Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
		}
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		return CPCapability.FOOD_INFO.getCapability(stack, null).getFood();
		
	}
}
