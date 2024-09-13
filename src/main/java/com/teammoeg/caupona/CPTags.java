package com.teammoeg.caupona;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CPTags {
	public static class Blocks{

		public static final TagKey<Block> FUMAROLE_HOT_BLOCK = create("fumarole_hot");
		public static final TagKey<Block> FUMAROLE_VERY_HOT_BLOCK = create("fumarole_very_hot");
		public static final TagKey<Block> FUMAROLE_BLOOM = create("fumarole_bloom");
		public static final TagKey<Block> HYPOCAUST_HEAT_CONDUCTOR = create("heat_conductor");
		public static final TagKey<Block> FRUITS_GROWABLE_ON = create("fruits_growable");
		public static final TagKey<Block> CHINMEY_BLOCK = create("chimney");
		public static final TagKey<Block> CHIMNEY_POT = create("chimney_pot");
		public static final TagKey<Block> CHIMNEY_IGNORES = create("chimney_ignore");
		public static final TagKey<Block> CALIDUCTS = create("caliducts");
		public static final TagKey<Block> PANS = create("pans");
		public static final TagKey<Block> COUNTERS = create("counter");
		public static final TagKey<Block> STOVES = create("stoves");
		public static final TagKey<Block> SNAIL_GROWABLE_ON = create("snail_growable");
		public static final TagKey<Block> SNAIL_FOOD = create("snail_food");
		public static final TagKey<Block> SNAIL_PLUMP_FOOD = create("snail_plump_food");
		public static final TagKey<Block> LOAF_HEATING_BLOCKS=create("loaf_baking_blocks");
		public static final TagKey<Block> LOAF_HEATING_IGNORE=create("loaf_baking_ignore");
		private static TagKey<Block> create(String s){
			return  BlockTags.create(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, s));
		}

	}
	public static class Items{

		public static final TagKey<Item> PORTABLE_BRAZIER_FUEL_TYPE = create("portable_brazier_fuel");
		public static final TagKey<Item> COOKABLE = create("cookable");
		public static final TagKey<Item> STEW_CONTAINER = create("container");
		public static final TagKey<Item> FUEL_WOODS = create("fuel/woods");
		public static final TagKey<Item> FUEL_CHARCOALS = create("fuel/charcoals");
		public static final TagKey<Item> FUEL_FOSSIL = create("fuel/fossil");
		public static final TagKey<Item> FUEL_LAVA = create("fuel/lava");
		public static final TagKey<Item> FUEL_OTHERS = create("fuel/others");
		public static final TagKey<Item> RICE = create("cereals/rice");
		public static final TagKey<Item> EGGS = create("eggs");
		public static final TagKey<Item> BAKED = create("cereals/baked");
		public static final TagKey<Item> VEGETABLES = create("vegetables");
		public static final TagKey<Item> MEAT = create("meats/meat");
		public static final TagKey<Item> FISH = create("seafood/fish");
		public static final TagKey<Item> POULTRY = create("meats/poultry");
		public static final TagKey<Item> SEAFOOD = create("seafood");
		public static final TagKey<Item> MEATS = create("meats");
		public static final TagKey<Item> SUGAR = create("sugar");
		public static final TagKey<Item> CEREALS = create("cereals");
		public static final TagKey<Item> CRUSTACEANS = create("seafood/crustaceans");
		public static final TagKey<Item> ROOTS = create("vegetables/roots");
		public static final TagKey<Item> MUSHROOMS = create("mushroom");
		public static final TagKey<Item> PUMPKIN = create("vegetables/pumpkin");
		public static final TagKey<Item> WALNUT = create("walnut");
		public static final TagKey<Item> GREENS = create("vegetables/greens");
		public static final TagKey<Item> CONTAINER = create("container");
		public static final TagKey<Item> MOSAIC_BASE = create("mosaic_bases");
		private static TagKey<Item> create(String s){
			return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, s));
		}

	}

	public static class Fluids{

		public static final TagKey<Fluid> STEWS = create("stews");
		public static final TagKey<Fluid> ANY_WATER = create("water");
		public static final TagKey<Fluid> BOILABLE = create("boilable");
		public static final TagKey<Fluid> PUMICE_ON = create("pumice_bloom_grow_on");
		private static TagKey<Fluid> create(String s){
			return FluidTags.create(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, s));
		}


	}

}
