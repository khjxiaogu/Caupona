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

package com.teammoeg.caupona.item;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class SkimmerItem extends CPItem {

	public SkimmerItem(Properties properties) {
		super(properties, TabType.MAIN);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		 BlockPos pos=context.getClickedPos();
		 BlockEntity be=context.getLevel().getBlockEntity(pos);
		 if(be instanceof StewPotBlockEntity stewpot) {
			 if(stewpot.canAddFluid()) {
				 FluidTank tank=stewpot.getTank();
				 FluidStack fluid=tank.getFluidInTank(0);
				 StewInfo si=Utils.getOrCreateInfo(fluid);
				 float dense=si.getDensity();
				 if(dense>0.5) {
					 float toreduce=Math.min(dense-0.5f,0.5f);
					 float reduced=toreduce*fluid.getAmount()/250f;
					 for(FloatemStack lstack:si.getStacks()) {
						 lstack.shrink(lstack.getCount()/dense*toreduce);
					 }
					 si.recalculateHAS();
					 Utils.setInfo(fluid, si);
					 tank.setFluid(fluid);
					 stack.hurtAndBreak( 1,context.getPlayer(),context.getHand()==InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);
					 float frac=Mth.frac(reduced);
					 int amt=Mth.floor(reduced);
					 if(context.getPlayer().getRandom().nextFloat()<frac)
						 amt++;
					 ItemHandlerHelper.giveItemToPlayer(context.getPlayer(), new ItemStack(CPItems.scraps.get(),amt));
					 return InteractionResult.SUCCESS;
				 }
				 
			 }
			 return InteractionResult.FAIL;
		 }
		 return InteractionResult.PASS;
	}


}
