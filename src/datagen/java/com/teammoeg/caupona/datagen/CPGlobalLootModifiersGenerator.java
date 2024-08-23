package com.teammoeg.caupona.datagen;

import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.loot.AddPoolLootModifier;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;


public class CPGlobalLootModifiersGenerator extends GlobalLootModifierProvider {

	public CPGlobalLootModifiersGenerator(PackOutput output,CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper, String name) {
		super(output,provider, CPMain.MODID);
	}

	@Override
	protected void start() {
		for(ResourceKey<LootTable> table:ImmutableSet.of(
				BuiltInLootTables.ABANDONED_MINESHAFT,
				BuiltInLootTables.BURIED_TREASURE,
				BuiltInLootTables.ANCIENT_CITY,
				BuiltInLootTables.DESERT_PYRAMID,
				BuiltInLootTables.IGLOO_CHEST,
				BuiltInLootTables.JUNGLE_TEMPLE,
				BuiltInLootTables.PILLAGER_OUTPOST,
				BuiltInLootTables.SHIPWRECK_TREASURE,
				BuiltInLootTables.SIMPLE_DUNGEON,
				BuiltInLootTables.STRONGHOLD_CORRIDOR,
				BuiltInLootTables.STRONGHOLD_CROSSING,
				BuiltInLootTables.UNDERWATER_RUIN_BIG,
				BuiltInLootTables.UNDERWATER_RUIN_SMALL,
				BuiltInLootTables.WOODLAND_MANSION
				)) {
			this.add(table.location().getPath(), AddPoolLootModifier.builder(CPLootGenerator.ASSES.location()).when(LootTableIdCondition.builder(table.location())).build());
		}
	}
}
