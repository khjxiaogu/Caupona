package com.teammoeg.caupona.item;

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.level.block.Block;

public class CPHangingSignItem extends HangingSignItem implements ICreativeModeTabItem {

	public CPHangingSignItem(Block block, Block wallBlock, Properties properties, TabType tab) {
		super(block, wallBlock, properties);
		this.tab = tab;
	}

	TabType tab;
	
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(tab))helper.accept(this);
	}


}
