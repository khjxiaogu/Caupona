package com.teammoeg.caupona.client;


import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.util.LayeredElementsModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = CPMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterAdditional ev)
	{
		Minecraft.getInstance().getResourceManager().listResources("models/block/dynamic",e->e.getPath().endsWith(".json")).keySet().forEach(rl->{
			ev.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(rl.getNamespace(),rl.getPath().substring(0,rl.getPath().lastIndexOf(".")).substring(7))));
		});
	}
	@SubscribeEvent
	public static void registerLoaders(ModelEvent.RegisterGeometryLoaders ev)
	{
		ev.register(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"layered"),new LayeredElementsModel.Loader());
	}
	
}
