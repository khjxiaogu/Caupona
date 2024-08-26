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
import java.util.function.Supplier;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluid;

public class StewItem extends EdibleBlock{

	@Override
	public int getUseDuration(ItemStack stack,LivingEntity entity) {
		return 16;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		StewInfo info = stack.get(CPCapability.STEW_INFO);
		if(info==null)return;
		FloatemStack fs = info.stacks.stream()
				.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
				.orElse(null);
		if (fs != null)
			tooltip.add(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
		ResourceLocation rl = info.spiceName;
		if (rl != null)
			tooltip.add(Utils.translate("tooltip.caupona.spice",
					Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
		;
		Fluid base = info.base;
		if (base != null&&!info.stacks.isEmpty())
			tooltip.add(Utils.translate("tooltip.caupona.base", 
					base.getFluidType().getDescription()));
		PotionContents.addPotionTooltip(info.effects, tooltip::add, 1,20);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isFoodTab()) {
			ItemStack is = new ItemStack(this);
			is.set(CPCapability.STEW_INFO, new StewInfo(fluid.get()));
			super.addCreativeHints(is);
			helper.accept(is);
		}
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	Supplier<Fluid> fluid;
	// fake food to trick mechanics
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).fast()
			.build();

	public StewItem(Supplier<Fluid> fluid, Properties properties) {
		super(CPBlocks.BOWL.get(), properties.food(fakefood));
		CPItems.stews.add(this);
		this.fluid = fluid;
	}


	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		StewInfo info = stack.get(CPCapability.STEW_INFO);
		if(info==null)return null;
		return info.getFood();
		
	}
}
