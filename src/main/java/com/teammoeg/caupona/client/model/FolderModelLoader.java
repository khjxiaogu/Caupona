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

package com.teammoeg.caupona.client.model;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FolderModelLoader implements IGeometryLoader<CompositeModel> {
    public static final FolderModelLoader INSTANCE = new FolderModelLoader();

    private FolderModelLoader() {}

    @Override
    public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException  {
    	
        ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
        Set<String> orderBuilder=new LinkedHashSet<>();
        if(jsonObject.has("order")) {
        	JsonArray ja=jsonObject.get("order").getAsJsonArray();
        	for(JsonElement i:ja) {
        		orderBuilder.add(i.getAsString());
        	}
        }
        //System.out.println("loading folder model ");
        if(jsonObject.has("folder")) {
            String folder=jsonObject.get("folder").getAsString();
            ResourceLocation rf=ResourceLocation.parse(folder);
            //System.out.println("loading from folder "+folder);
            for(Entry<ResourceLocation, Resource> i:Minecraft.getInstance().getResourceManager().listResources("models/"+rf.getPath(),e->true).entrySet()) {
            	//System.out.println("loading "+i.getKey()+" from folder.");
            	try (BufferedReader r=i.getValue().openAsReader()){
					BlockModel bm=BlockModel.fromStream(r);
					childrenBuilder.put(i.getKey().getPath(), bm);
				} catch (IOException e1) {
					throw new JsonParseException(e1);
				}
            	orderBuilder.add(i.getKey().getPath());
            }
        }else {
        	
        }
       
        var children = childrenBuilder.build();
  
        return new CompositeModel(children, ImmutableList.copyOf(orderBuilder));
    }


}