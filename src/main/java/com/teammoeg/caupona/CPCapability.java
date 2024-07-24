package com.teammoeg.caupona;

import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicData;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CPCapability {
	public static final ItemCapability<IFoodInfo, Void> FOOD_INFO=ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"food_info"), IFoodInfo.class);
	public static final DeferredRegister<DataComponentType<?>> REGISTRY=DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CPMain.MODID);
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<StewInfo>> STEW_INFO=REGISTRY.register(
		"stew_info",
		()->DataComponentType.<StewInfo>builder().persistent(StewInfo.CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SauteedFoodInfo>> SAUTEED_INFO=REGISTRY.register(
		"sauteed_info",
		()->DataComponentType.<SauteedFoodInfo>builder().persistent(SauteedFoodInfo.CODEC).build());
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> SIMPLE_FLUID=REGISTRY.register(
		"fluid_data",
		()->DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<MosaicData>> MOSAIC_DATA=REGISTRY.register(
		"mosaic_data",
		()->DataComponentType.<MosaicData>builder().persistent(MosaicData.CODEC).networkSynchronized(MosaicData.STREAM_CODEC).build());
	
	public CPCapability() {
	}
}
