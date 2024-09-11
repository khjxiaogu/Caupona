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

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EdibleBlock extends CPBlockItem {

	public EdibleBlock(Block block, Properties props) {
		super(block, props,TabType.FOODS);
	}


	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isFoodTab()) {
			ItemStack is=new ItemStack(this);
			
			
			addCreativeHints(is);
			helper.accept(is);
		}
	}
	public void addCreativeHints(ItemStack stack) {
		ItemLore lc=stack.get(DataComponents.LORE);
		lc=lc.withLineAdded(Utils.translate("tooltip.caupona.display_only")).withLineAdded(Utils.translate("tooltip.caupona.cook_required"));
		stack.set(DataComponents.LORE, lc);
	}
	/**
	 * Returns the unlocalized name of this item.
	 */
	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	/**
	 * Called when this item is used when targetting a Block
	 */
	@SuppressWarnings("resource")
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult interactionresult = InteractionResult.PASS;
		if (pContext.getPlayer().isShiftKeyDown())
			interactionresult = this.place(new BlockPlaceContext(pContext));
		// if(!pContext.getPlayer().getCooldowns().isOnCooldown(CPItems.water))
		if (!interactionresult.consumesAction()) {

			InteractionResult interactionresult1 = this
					.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
			return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL
					: interactionresult1;
		}
		return interactionresult;
	}

}