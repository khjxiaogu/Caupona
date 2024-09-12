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

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.decoration.BaseColumnBlock;
import com.teammoeg.caupona.blocks.decoration.CPButtonBlock;
import com.teammoeg.caupona.blocks.decoration.CPCeilingHangingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPPressurePlateBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadSideBlock;
import com.teammoeg.caupona.blocks.decoration.CPStandingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPTrapDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPWallHangingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPWallSignBlock;
import com.teammoeg.caupona.blocks.decoration.ChimneyFluteBlock;
import com.teammoeg.caupona.blocks.decoration.ColumnCapitalBlock;
import com.teammoeg.caupona.blocks.decoration.LacunarBlock;
import com.teammoeg.caupona.blocks.decoration.SpokedFenceBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicItem;
import com.teammoeg.caupona.blocks.decoration.mosaic.TessellationWorkBenchBlock;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlock;
import com.teammoeg.caupona.blocks.foods.BowlBlock;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleBoulderBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlock;
import com.teammoeg.caupona.blocks.fumarole.PumiceBloomBlock;
import com.teammoeg.caupona.blocks.hypocaust.CaliductBlock;
import com.teammoeg.caupona.blocks.hypocaust.FireboxBlock;
import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlock;
import com.teammoeg.caupona.blocks.loaf.LoafBlock;
import com.teammoeg.caupona.blocks.loaf.LoafDoughBlock;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.pan.PanBlock;
import com.teammoeg.caupona.blocks.plants.BushLogBlock;
import com.teammoeg.caupona.blocks.plants.CPStripPillerBlock;
import com.teammoeg.caupona.blocks.plants.FruitBlock;
import com.teammoeg.caupona.blocks.plants.FruitsLeavesBlock;
import com.teammoeg.caupona.blocks.plants.SilphiumBlock;
import com.teammoeg.caupona.blocks.plants.SnailBaitBlock;
import com.teammoeg.caupona.blocks.plants.SnailBlock;
import com.teammoeg.caupona.blocks.plants.WalnutFruitBlock;
import com.teammoeg.caupona.blocks.pot.StewPot;
import com.teammoeg.caupona.blocks.stove.ChimneyPotBlock;
import com.teammoeg.caupona.blocks.stove.KitchenStove;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.item.CPHangingSignItem;
import com.teammoeg.caupona.item.CPSignItem;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.item.LoafDishItem;
import com.teammoeg.caupona.util.MaterialType;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, CPMain.MODID);
	// static string data
	public static final String[] woods = new String[] { "walnut" };
	// Dynamic block types
	public static final MaterialType[] all_materials = new MaterialType[] {
			new MaterialType("loaf_heap").makeDecoration(),
			new MaterialType("mud").makeCounter(1),
			new MaterialType("stone_brick").makeCounter(2).makeHypocaust(),
			new MaterialType("stone").setBase(()->Blocks.STONE.defaultBlockState()).makePillar().makeRoad(),
			new MaterialType("sandstone").setBase(()->Blocks.SANDSTONE.defaultBlockState()).makeRoad(),
			new MaterialType("brick").makeCounter(2).makeHypocaust(),
			new MaterialType("mixed_bricks").makeDecoration(),
			new MaterialType("opus_incertum").makeCounter(2).makeDecoration().makeHypocaust(),
			new MaterialType("opus_latericium").makeCounter(2).makeDecoration().makeHypocaust(),
			new MaterialType("opus_reticulatum").makeDecoration(),
			new MaterialType("felsic_tuff_bricks").makeDecoration(),
			new MaterialType("felsic_tuff").makeDecoration().makePillar().makeRoad(),
			new MaterialType("polished_felsic_tuff").makeDecoration(),
			new MaterialType("quartz").makePillar(),
			new MaterialType("calcite").makePillar() };
	// Block Lists for use in other registries
	public static final List<DeferredHolder<Block,KitchenStove>> stoves = new ArrayList<>();
	public static final List<Block> signs = new ArrayList<>();
	public static final List<Block> hanging_signs = new ArrayList<>();
	public static final Map<String, DeferredHolder<Block,Block>> stoneBlocks = new HashMap<>();
	public static final List<Block> chimney = new ArrayList<>();
	public static final List<Block> dolium = new ArrayList<>();
	public static final List<Block> dishes = new ArrayList<>();
	public static final List<Block> caliduct = new ArrayList<>();
	public static final List<Block> firebox = new ArrayList<>();
	public static final List<DeferredHolder<Block,Block>> leaves = new ArrayList<>();

	// Other useful blocks
	public static final DeferredHolder<Block,FumaroleBoulderBlock> FUMAROLE_BOULDER = decoblock("fumarole_boulder",
			() -> new FumaroleBoulderBlock(getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion()
					.isSuffocating(CPBlocks::isntSolid)));
	public static final DeferredHolder<Block,FumaroleVentBlock> FUMAROLE_VENT = maindecoblock("fumarole_vent",
			() -> new FumaroleVentBlock(getStoneProps().strength(4.5f, 10).isViewBlocking(CPBlocks::isntSolid)
					.noOcclusion().isSuffocating(CPBlocks::isntSolid)));
	public static final DeferredHolder<Block,Block> PUMICE = block("pumice", getStoneProps(),TabType.DECORATION);
	public static final DeferredHolder<Block,PumiceBloomBlock> PUMICE_BLOOM = maindecoblock("pumice_bloom",
			() -> new PumiceBloomBlock(getStoneProps().noOcclusion()));
	public static final DeferredHolder<Block,GravyBoatBlock> GRAVY_BOAT = BLOCKS.register("gravy_boat",
			() -> new GravyBoatBlock(Block.Properties.of().sound(SoundType.GLASS).instabreak().noOcclusion()
					.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid)));
	public static final BlockSetType WALNUT_TYPE = new BlockSetType("walnut");
	public static final WoodType WALNUT = WoodType.register(new WoodType("caupona:walnut", WALNUT_TYPE));
	public static final DeferredHolder<Block,WolfStatueBlock> WOLF = maindecoblock("wolf_statue",
			() -> new WolfStatueBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER)
					.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block,PanBlock> STONE_PAN = mainblock("stone_griddle", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block,TessellationWorkBenchBlock> T_BENCH= mainblock("tessellation_workbench",()->new TessellationWorkBenchBlock
			(Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.STONE).strength(3.5f, 10).noOcclusion().requiresCorrectToolForDrops()));
	public static final DeferredHolder<Block,PanBlock> COPPER_PAN = mainblock("copper_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block,PanBlock> IRON_PAN = mainblock("iron_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block,PanBlock> LEAD_PAN = mainblock("lead_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block,DishBlock> DISH = BLOCKS.register("dish",
			() -> new DishBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid)));
	public static final DeferredHolder<Block,StewPot> STEW_POT = mainblock("stew_pot",
			() -> new StewPot(
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),
					CPBlockEntityTypes.STEW_POT));
	public static final DeferredHolder<Block,StewPot> STEW_POT_LEAD = mainblock("lead_stew_pot",
			() -> new StewPot(
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),
					CPBlockEntityTypes.STEW_POT));

	public static final DeferredHolder<Block,BowlBlock> BOWL = BLOCKS.register("bowl",
			() -> new BowlBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid), CPBlockEntityTypes.BOWL));
	public static final DeferredHolder<Block,BowlBlock> LOAF_BOWL = 
		
		foodblock("loaf_bowl",
		() -> new BowlBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
				.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
				.isViewBlocking(CPBlocks::isntSolid), CPBlockEntityTypes.BOWL));
	
	public static final DeferredHolder<Block,MosaicBlock> MOSAIC = baseblock("mosaic",
			() -> new MosaicBlock(getStoneProps()),b->new MosaicItem(CPItems.createProps()));
	public static final DeferredHolder<Block,SilphiumBlock> SILPHIUM = mainblock("silphium_block",
			() -> new SilphiumBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission()
					.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava()
					.pushReaction(PushReaction.DESTROY)));
	public static final DeferredHolder<Block,Block> WALNUT_FRUIT = mainblock("walnut_fruits", () -> new WalnutFruitBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).ignitedByLava()));
	public static final DeferredHolder<Block,Block> SNAIL_MUCUS=block("snail_mucus",BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).randomTicks().instabreak().sound(SoundType.CROP).noOcclusion().isViewBlocking(CPBlocks::isntSolid),TabType.MAIN_AND_DECORATION);

	public static final DeferredHolder<Block,SnailBlock> SNAIL = baseblock("snail_block", ()->new SnailBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid)),x->new CPBlockItem(x,CPItems.createProps(),TabType.MAIN));
	public static final DeferredHolder<Block,SnailBaitBlock> SNAIL_BAIT = baseblock("snail_bait", ()->new SnailBaitBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid)),x->new CPBlockItem(x,CPItems.createProps(),TabType.MAIN));
	public static final DeferredHolder<Block,Block> LEAD_BLOCK=block("lead_block",BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).requiresCorrectToolForDrops(),TabType.DECORATION);
	public static final DeferredHolder<Block,LoafDoughBlock> LOAF_DOUGH=foodblock("loaf_dough",()->new LoafDoughBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOL).instabreak().noOcclusion()
		.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
		.isViewBlocking(CPBlocks::isntSolid)));
	public static final DeferredHolder<Block,SlabBlock> LOAF=foodblock("loaf",()->new LoafBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOL).instabreak().noOcclusion()
		.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
		.isViewBlocking(CPBlocks::isntSolid)));

	// Bulk register blocks
	static {

		for (MaterialType type : all_materials) {
			String name = type.getName();
			if (type.isDecorationMaterial()) {
				DeferredHolder<Block,Block> base = block(name, getStoneProps(),TabType.DECORATION);
				stoneBlocks.put(name, base);
				type.setBase(()->base.get().defaultBlockState());
				decoblock(name + "_slab", () -> new SlabBlock(getStoneProps()));
				decoblock(name + "_stairs", () -> new StairBlock(type.getBase().get(), getStoneProps()));
				decoblock(name + "_wall", () -> new WallBlock(getStoneProps()));
			}
			if (type.isCounterMaterial()) {
				stove(name + "_kitchen_stove", getStoveProps(),
						type.getCounterGrade() == 1 ? CPBlockEntityTypes.STOVE_T1 : CPBlockEntityTypes.STOVE_T2);
				maindecoblock(name + "_chimney_flue", ()->new ChimneyFluteBlock(getTransparentProps()));
				maindecoblock(name + "_chimney_pot", () -> new ChimneyPotBlock(getTransparentProps()));
				decoblock(name + "_counter", () -> new CPHorizontalBlock(getStoneProps()));
				maindecoblock(name + "_counter_with_dolium", () -> new CounterDoliumBlock(getTransparentProps()));
			}
			if (type.isHypocaustMaterial()) {
				mainblock(name + "_caliduct", () -> new CaliductBlock(getTransparentProps()));
				mainblock(name + "_hypocaust_firebox", () -> new FireboxBlock(getTransparentProps()));
			}
			if (type.isPillarMaterial()) {
				decoblock(name + "_column_fluted_plinth",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_column_fluted_shaft",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_column_shaft",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_column_plinth",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_ionic_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_tuscan_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_acanthine_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_lacunar_tile",()->new LacunarBlock(getTransparentProps().strength(2f, 6f)
						.isViewBlocking(CPBlocks::isntSolid)));
				decoblock(name+"_spoked_fence",()->new SpokedFenceBlock(getTransparentProps().strength(2f, 6f)));
			}
			if(type.isRoadMaterial()) {
				decoblock(name+"_road_side",()->new CPRoadSideBlock(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)));
				decoblock(name+"_road",()->new CPRoadBlock(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)));
			}
		}

		registerWood("walnut", WALNUT,()-> new TreeGrower("walnut",Optional.empty(),Optional.of(CPWorldGen.WALNUT),Optional.empty()),WALNUT_FRUIT);
		registerBush("fig", ()-> new TreeGrower("fig",Optional.empty(),Optional.of(CPWorldGen.FIG),Optional.empty()));
		registerBush("wolfberry", ()-> new TreeGrower("wolfberry",Optional.empty(),Optional.of(CPWorldGen.WOLFBERRY),Optional.empty()));
		for (String s : CPItems.dishes) {
			baseblock(s,
				() -> new DishBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
							.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
							.isViewBlocking(CPBlocks::isntSolid)),
					b -> new DishItem(b, CPItems.createSoupProps().get()));
			baseblock(s+"_loaf",
				() -> new DishBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
							.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
							.isViewBlocking(CPBlocks::isntSolid)),
					b -> new LoafDishItem(b, CPItems.createLoafSoupProps().get()));
		}
	}
	// Convenient block registry wrapper

	// create a bush
	private static void registerBush(String wood, Supplier<TreeGrower> growth) {
		decoblock(wood + "_log", () -> new BushLogBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(2.0F).noOcclusion().sound(SoundType.WOOD)));
		DeferredHolder<Block,Block> a = decoblock(wood + "_fruits", () -> new FruitBlock(BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT).offsetType(OffsetType.XZ).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
		leaves.add(
				CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves", () -> leaves(SoundType.GRASS, a)), 0.3F));

		CPCommonBootStrap.asCompositable(
				maindecoblock(wood + "_sapling", () -> new SaplingBlock(growth.get(), BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS))),
				0.3F);

	}

	// create a wood
	private static void registerWood(String wood, WoodType wt, Supplier<TreeGrower> growth,DeferredHolder<Block,Block> f) {
		DeferredHolder<Block,Block> planks = CPCommonBootStrap.asFlamable(block(wood + "_planks",
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),TabType.DECORATION)
				,5,20);
		decoblock(wood + "_button",
				() -> new CPButtonBlock(
						BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(), WALNUT_TYPE,
						30));
		decoblock(wood + "_door", () -> new CPDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(3.0F).sound(SoundType.WOOD).noOcclusion().ignitedByLava(), WALNUT_TYPE));
		CPCommonBootStrap.asFlamable(decoblock(wood + "_fence", () -> new FenceBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava())),5,20);
		CPCommonBootStrap.asFlamable(decoblock(wood + "_fence_gate", () -> new FenceGateBlock(
				WALNUT,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava())),5,20);

		leaves.add(CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves", () -> leaves(SoundType.GRASS, f)), 0.3F));
		DeferredHolder<Block,Block> sl = CPCommonBootStrap.asFlamable(decoblock("stripped_" + wood + "_log", () -> log(null)),5,5);
		CPCommonBootStrap.asFlamable(decoblock(wood + "_log", () -> log(sl)),5,5);

		decoblock(wood + "_pressure_plate",
				() -> new CPPressurePlateBlock(BlockBehaviour.Properties.of()
								.mapColor(MapColor.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(),
						WALNUT_TYPE));
		CPCommonBootStrap.asCompositable(CPCommonBootStrap.asFlamable(
				maindecoblock(wood + "_sapling", () -> new SaplingBlock(growth.get(), BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).ignitedByLava())),5,5),
				0.3F);
		DeferredHolder<Block,Block> s = BLOCKS.register(wood + "_sign",
				() -> new CPStandingSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollission()
						.strength(1.0F).sound(SoundType.WOOD).ignitedByLava(), wt));
		
		DeferredHolder<Block,Block> ws = BLOCKS.register(wood + "_wall_sign",
				() -> new CPWallSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollission()
						.strength(1.0F).sound(SoundType.WOOD).ignitedByLava(), wt));
		 
		DeferredHolder<Block,Block> hs = CPCommonBootStrap.asFlamable(BLOCKS.register(wood + "_hanging_sign",
			() -> new CPCeilingHangingSignBlock(
	            wt,
	            BlockBehaviour.Properties.of()
	                .mapColor(MapColor.WOOD)
	                .forceSolidOn()
	                .instrument(NoteBlockInstrument.BASS)
	                .noCollission()
	                .strength(1.0F)
	                .ignitedByLava()
	        )),5,5);
		DeferredHolder<Block,Block> whs = CPCommonBootStrap.asFlamable(BLOCKS.register(wood + "_wall_hanging_sign",
			() -> new CPWallHangingSignBlock(
	            wt,
	            BlockBehaviour.Properties.of()
	                .mapColor(MapColor.WOOD)
	                .forceSolidOn()
	                .instrument(NoteBlockInstrument.BASS)
	                .noCollission()
	                .strength(1.0F)
	                .ignitedByLava()
	        )),5,5);
		

		CPItems.ITEMS.register(wood + "_sign",
				() -> new CPSignItem((new Item.Properties()).stacksTo(16), s.get(), ws.get(), TabType.DECORATION));
		CPItems.ITEMS.register(wood + "_hanging_sign",
			() -> new CPHangingSignItem(hs.get(), whs.get(),(new Item.Properties()).stacksTo(16), TabType.DECORATION));
		
		CPCommonBootStrap.asFlamable(decoblock(wood + "_slab", () -> new SlabBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava())),5,20);
		CPCommonBootStrap.asFlamable(decoblock(wood + "_stairs", () -> new StairBlock(planks.get().defaultBlockState(),
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD))),5,20);
		CPCommonBootStrap.asFlamable(decoblock(wood + "_trapdoor", () -> new CPTrapDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(CPBlocks::never).ignitedByLava(), WALNUT_TYPE)),5,5);
		DeferredHolder<Block,Block> sw = CPCommonBootStrap.asFlamable(decoblock("stripped_" + wood + "_wood", () -> new RotatedPillarBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava())),5,5);
		CPCommonBootStrap.asFlamable(decoblock(wood + "_wood", () -> new CPStripPillerBlock(sw,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava())),5,5);
	}

	// create a stove
	static DeferredHolder<Block,KitchenStove> stove(String name, Properties props,
			DeferredHolder<BlockEntityType<?>,BlockEntityType<KitchenStoveBlockEntity>> tile) {
		DeferredHolder<Block,KitchenStove> bl = BLOCKS.register(name, () -> new KitchenStove(props, tile));
		stoves.add(bl);

		CPItems.ITEMS.register(name, () -> new CPBlockItem(bl.get(), CPItems.createProps(), TabType.MAIN_AND_DECORATION));
		return bl;
	}

	// register any block to caupona registry
	static <T extends Block> DeferredHolder<Block,T> mainblock(String name, Supplier<T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.MAIN));
		return blx;
	}
	// register any block to caupona registry
	static <T extends Block> DeferredHolder<Block,T> foodblock(String name, Supplier<T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.FOODS));
		return blx;
	}
	static <T extends Block> DeferredHolder<Block,T> decoblock(String name, Supplier<T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.DECORATION));
		return blx;
	}
	static <T extends Block> DeferredHolder<Block,T> maindecoblock(String name, Supplier<T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.MAIN_AND_DECORATION));
		return blx;
	}

	// register any block to caupona registry with custom item factory
	static <T extends Block> DeferredHolder<Block,T> baseblock(String name, Supplier<T> bl, Function<T, Item> toitem) {
		DeferredHolder<Block,T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> toitem.apply(blx.get()));
		return blx;
	}

	// register basic block to caupona registry
	static DeferredHolder<Block,Block> block(String name, Properties props,TabType tab) {
		DeferredHolder<Block,Block> blx = BLOCKS.register(name, () -> new Block(props));
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), tab));
		return blx;
	}

	// Make leaves block
	private static LeavesBlock leaves(SoundType p_152615_, DeferredHolder<Block,Block> fruit) {
		return new FruitsLeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F)
				.randomTicks().sound(p_152615_).noOcclusion().isValidSpawn(CPBlocks::ocelotOrParrot)
				.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid).ignitedByLava(), fruit);
	}

	// Make log block
	private static RotatedPillarBlock log(DeferredHolder<Block,Block> st) {
		if (st == null)
			return new RotatedPillarBlock(
					BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
		return new CPStripPillerBlock(st,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
	}

	// Property functions
	private static Properties getStoneProps() {
		return Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(2.0f, 6);
	}

	private static Properties getStoveProps() {
		return Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(3.5f, 10).noOcclusion().lightLevel(s -> s.getValue(KitchenStove.LIT) ? 9 : 0)
				.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid);
	}

	private static Properties getTransparentProps() {
		return Block.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.5f, 10)
				.noOcclusion();
	}

	private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

	private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
		return (boolean) false;
	}

	private static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_,
			EntityType<?> p_50825_) {
		return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
	}
}