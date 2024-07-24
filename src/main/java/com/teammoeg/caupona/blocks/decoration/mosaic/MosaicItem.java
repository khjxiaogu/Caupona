package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.client.renderer.MosaicRenderer;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

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
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.material_1",Utils.translate("item.caupona."+tag.material1+"_tesserae")));
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.material_2",Utils.translate("item.caupona."+tag.material2+"_tesserae")));
			tooltipComponents.add(Utils.translate("tooltip.caupona.mosaic.pattern."+tag.pattern));
		}
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
	}
	public static void setMosaic(ItemStack stack,MosaicMaterial m1,MosaicMaterial m2,MosaicPattern p) {
		stack.set(CPCapability.MOSAIC_DATA, new MosaicData(p,m1,m2));

	}
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			MosaicRenderer renderer=new MosaicRenderer();
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
			
		});
	}


}
