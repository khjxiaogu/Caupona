package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

public enum MosaicMaterial implements StringRepresentable{
	brick("t"),
	basalt("b"),
	pumice("p");
	public final String shortName;
	private final Lazy<Item> tell;
	private MosaicMaterial(String shortName) {
		this.shortName = shortName;
		this.tell=Utils.itemSupplier(name()+"_tesserae");
	}

	private Item getTesserae() {
		return tell.get();
	}
	private static Map<Item,MosaicMaterial> materials;
	public static MosaicMaterial fromItem(ItemStack is) {
		if(materials==null) {
			materials=ImmutableMap.of(brick.getTesserae(),brick,basalt.getTesserae(),basalt,pumice.getTesserae(),pumice);
		}
		return materials.get(is.getItem());
	}
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
