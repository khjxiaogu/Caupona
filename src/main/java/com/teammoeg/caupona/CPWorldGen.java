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

import com.teammoeg.caupona.worldgen.BushStraightTrunkPlacer;
import com.teammoeg.caupona.worldgen.FumaroleStructure;
import com.teammoeg.caupona.worldgen.LeavingLogReplacer;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPWorldGen {
	public static final DeferredRegister<TreeDecoratorType<?>> FOILAGE_TYPES = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, CPMain.MODID);
	public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, CPMain.MODID);
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, CPMain.MODID);

	public static final DeferredHolder<TreeDecoratorType<?>,TreeDecoratorType<LeavingLogReplacer>> BUSH_PLACER = FOILAGE_TYPES.register("leaving_log_replacer", () -> new TreeDecoratorType<>(LeavingLogReplacer.CODEC));

	public static final DeferredHolder<TrunkPlacerType<?>,TrunkPlacerType<BushStraightTrunkPlacer>> BUSH_TRUNK = TRUNK_TYPES.register("bush_chunk", () -> new TrunkPlacerType<>(BushStraightTrunkPlacer.CODEC));

	public static final DeferredHolder<StructureType<?>,StructureType<FumaroleStructure>> FUMAROLE = STRUCTURE_TYPES.register("fumarole",()->()->FumaroleStructure.CODEC);

	public static final ResourceKey<PlacedFeature> TREES_WALNUT = createPlacementKey("trees_walnut");
	public static final ResourceKey<PlacedFeature> TREES_FIG = createPlacementKey("trees_fig");
	public static final ResourceKey<PlacedFeature> TREES_WOLFBERRY = createPlacementKey("trees_wolfberry");

	public static final ResourceKey<PlacedFeature> PATCH_SILPHIUM = createPlacementKey("patch_silphium");

	public static final ResourceKey<ConfiguredFeature<?, ?>> WALNUT = createFeatureKey("walnut");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FIG = createFeatureKey("fig");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WOLFBERRY = createFeatureKey("wolfberry");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SILPHIUM = createFeatureKey("silphium");


    public static ResourceKey<PlacedFeature> createPlacementKey(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(CPMain.MODID,key));
    }
    public static ResourceKey<ConfiguredFeature<?,?>> createFeatureKey(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(CPMain.MODID,key));
    }
	public CPWorldGen() {
	}




}
