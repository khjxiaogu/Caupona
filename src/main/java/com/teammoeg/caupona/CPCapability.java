package com.teammoeg.caupona;

import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicData;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.ItemHoldedFluidData;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.StewInfo;
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
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<StewInfo>> STEW_INFO=REGISTRY.register(
		"stew_info",
		()->DataComponentType.<StewInfo>builder().persistent(StewInfo.CODEC).networkSynchronized(SerializeUtil.toStreamCodec(StewInfo.CODEC)).build());
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
