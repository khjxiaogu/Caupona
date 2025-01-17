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

package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.MosaicData;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class MosaicItem extends CPBlockItem {

	public MosaicItem(Properties props) {
		super(CPBlocks.MOSAIC.get(), props,TabType.DECORATION);
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		/*if(helper.isType(TabType.DECORATION)) {
			for(MosaicMaterial m1:MosaicMaterial.values())
				for(MosaicMaterial m2:MosaicMaterial.values())
					for(MosaicPattern pattern:MosaicPattern.values()) {
						if(m1==m2)continue;
						ItemStack stack=new ItemStack(this);
						setMosaic(stack, m1, m2, pattern);
						helper.accept(stack);
					}
			
		}*/
			
	}
	@Override
	 public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)  {
		@Nullable MosaicData tag=stack.get(CPCapability.MOSAIC_DATA);
		if(tag!=null) {
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.material_1",Utils.translate("item.caupona."+tag.getMaterial1()+"_tesserae")));
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.material_2",Utils.translate("item.caupona."+tag.getMaterial2()+"_tesserae")));
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.pattern."+tag.getPattern()));
		}
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
	}
	public static void setMosaic(ItemStack stack,MosaicMaterial m1,MosaicMaterial m2,MosaicPattern p) {
		stack.set(CPCapability.MOSAIC_DATA, new MosaicData(p,m1,m2));

	}


}
