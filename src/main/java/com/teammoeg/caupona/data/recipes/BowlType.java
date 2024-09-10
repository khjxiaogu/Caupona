package com.teammoeg.caupona.data.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record BowlType(ResourceLocation type) implements StringRepresentable{
	private static final Map<ResourceLocation,BowlType> typeIndex=new ConcurrentHashMap<>();
	public static final Codec<BowlType> REFERENCE_CODEC=ResourceLocation.CODEC.comapFlatMap(BowlType::parse, BowlType::type);
	public static final Codec<BowlType> GET_OR_CREATE_CODEC=ResourceLocation.CODEC.xmap(BowlType::getOrCreateByName, BowlType::type);
	
	public BowlType register(){
		typeIndex.put(type,this);
		return this;
	}
	public static BowlType getByName(ResourceLocation name){
		BowlType type= typeIndex.get(name);
		return type;
	}
	public static DataResult<BowlType> parse(ResourceLocation name){
		BowlType type= typeIndex.get(name);
		if(type==null)
			return DataResult.error(()->"BowlType "+name+"not exists!");
		return DataResult.success(type);
	}
	public static BowlType getOrCreateByName(ResourceLocation name){
		return typeIndex.computeIfAbsent(name, o->new BowlType(o));
	}
	public ResourceLocation type() {
		return type;
	}
	@Override
	public String getSerializedName() {
		return type.toString();
	}
}
