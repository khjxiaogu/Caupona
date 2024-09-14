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

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CPBlockItem extends BlockItem implements ICreativeModeTabItem{
	TabType tab;
	public CPBlockItem(Block block, Item.Properties props,TabType tab) {
		super(block, props);
		this.tab=tab;
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(super.getBlock() instanceof ICreativeModeTabItem item) {
			item.fillItemCategory(helper);
		}else if(helper.isType(tab))
			helper.accept(this);
	}

}
