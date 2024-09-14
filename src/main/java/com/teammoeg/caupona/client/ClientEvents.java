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

package com.teammoeg.caupona.client;


import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.model.FolderModelLoader;
import com.teammoeg.caupona.client.model.LayeredElementsModel;
import com.teammoeg.caupona.client.model.RotatedElementsModel;

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
		ev.register(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"rotated"),new RotatedElementsModel.Loader());
		ev.register(ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"folder"),FolderModelLoader.INSTANCE);
	}
	
}
