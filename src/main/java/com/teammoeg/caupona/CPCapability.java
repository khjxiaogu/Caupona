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

package com.teammoeg.caupona;

import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.components.ImmutableStewInfo;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.components.MosaicData;
import com.teammoeg.caupona.components.SauteedFoodInfo;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPCapability {
	public static final ItemCapability<IFoodInfo, Void> FOOD_INFO=ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"food_info"), IFoodInfo.class);
	public static final DeferredRegister<DataComponentType<?>> REGISTRY=DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CPMain.MODID);
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ImmutableStewInfo>> STEW_INFO=REGISTRY.register(
		"stew_info",
		()->DataComponentType.<ImmutableStewInfo>builder().persistent(ImmutableStewInfo.CODEC).networkSynchronized(SerializeUtil.toStreamCodec(StewInfo.CODEC)).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SauteedFoodInfo>> SAUTEED_INFO=REGISTRY.register(
		"sauteed_info",
		()->DataComponentType.<SauteedFoodInfo>builder().persistent(SauteedFoodInfo.CODEC).networkSynchronized(SerializeUtil.toStreamCodec(SauteedFoodInfo.CODEC)).build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> SIMPLE_FLUID=REGISTRY.register(
		"fluid_data",
		()->DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<MosaicData>> MOSAIC_DATA=REGISTRY.register(
		"mosaic_data",
		()->DataComponentType.<MosaicData>builder().persistent(MosaicData.CODEC).networkSynchronized(MosaicData.STREAM_CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemHoldedFluidData>> ITEM_FLUID=REGISTRY.register("fluid_type",
		()->DataComponentType.<ItemHoldedFluidData>builder().persistent(ItemHoldedFluidData.CODEC).networkSynchronized(ItemHoldedFluidData.STREAM_CODEC).build());
	public CPCapability() {
	}
}
