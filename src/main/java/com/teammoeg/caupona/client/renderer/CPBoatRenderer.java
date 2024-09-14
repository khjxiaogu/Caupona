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

package com.teammoeg.caupona.client.renderer;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.entity.CPBoat;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CPBoatRenderer extends BoatRenderer {
	private final Map<String, Pair<ResourceLocation,ListModel<Boat>>> boatResources;

	public CPBoatRenderer(Context p_173936_,boolean b) {
		super(p_173936_, b);
		
		boatResources = Stream.of(CPBlocks.woods).collect(ImmutableMap.toImmutableMap(s -> s, s ->{
			ModelPart mp=p_173936_.bakeLayer(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "boat/" + s), "main"));
			return Pair.of(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/entity/boat/" + s + ".png"),(b?new ChestBoatModel(mp):new BoatModel(mp)));
				
		}));

	}

	@Override
	public Pair<ResourceLocation,ListModel<Boat>> getModelWithLocation(Boat boat) {
		if (boat instanceof CPBoat) {
			return boatResources.get(((CPBoat) boat).getWoodType());
		}
		return super.getModelWithLocation(boat);
	}

}