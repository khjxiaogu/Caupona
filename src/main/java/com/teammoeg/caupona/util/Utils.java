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

package com.teammoeg.caupona.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.api.events.EventResult;
import com.teammoeg.caupona.api.events.FoodExchangeItemEvent;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.components.StewInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

public class Utils {

	public static final Direction[] horizontals = new Direction[] { Direction.EAST, Direction.WEST, Direction.SOUTH,
			Direction.NORTH };
	public static final String FLUID_TAG_KEY="caupona:fluid";
	/*public static final Codec<MobEffectInstance> MOB_EFFECT_CODEC=RecordCodecBuilder.create(u->u.group(
		BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(o->o.getEffect()),
		Codec.INT.fieldOf("time").forGetter(o->o.getDuration()),
		Codec.INT.fieldOf("level").forGetter(o->o.getAmplifier())
		).apply(u,MobEffectInstance::new));
	public static final Codec<Pair<MobEffectInstance,Float>> MOB_EFFECT_FLOAT_CODEC=
		RecordCodecBuilder.create(u->u.group(
		BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(o->o.getFirst().getEffect()),
		Codec.INT.fieldOf("time").forGetter(o->o.getFirst().getDuration()),
		Codec.INT.fieldOf("level").forGetter(o->o.getFirst().getAmplifier()),
		Codec.FLOAT.fieldOf("chance").forGetter(o->o.getSecond())
		).apply(u,(a,b,c,d)->Pair.of(new MobEffectInstance(a,b,c), d)));*/
	private Utils() {
	}
	public static <K,V> Codec<Pair<K,V>> pairCodec(String nkey,Codec<K> key,String nval,Codec<V> val){
		return RecordCodecBuilder.create(t->t.group(key.fieldOf(nkey).forGetter(Pair::getFirst), val.fieldOf(nval).forGetter(Pair::getSecond))
			.apply(t,Pair::of));
	} 
	public static <K,V> Codec<Map<K,V>> mapCodec(Codec<K> keyCodec,Codec<V> valueCodec){
		return Codec.compoundList(keyCodec, valueCodec).xmap(pl->pl.stream().collect(Collectors.toMap(Pair::getFirst,Pair::getSecond)),pl->pl.entrySet().stream().map(ent->Pair.of(ent.getKey(), ent.getValue())).toList()); 
	}
	public static ContanerContainFoodEvent contain(ItemStack its2,FluidStack fs,boolean simulate){
		ContanerContainFoodEvent ev=new ContanerContainFoodEvent(its2,fs,simulate,false);
		NeoForge.EVENT_BUS.post(ev);
		return ev;
	}
	public static ContanerContainFoodEvent containBlock(ItemStack its2,FluidStack fs){
		ContanerContainFoodEvent ev=new ContanerContainFoodEvent(its2,fs,false,true);
		NeoForge.EVENT_BUS.post(ev);
		return ev;
	}
	public static FluidStack extractFluid(ItemStack stack) {
		ItemHoldedFluidData si=stack.get(CPCapability.ITEM_FLUID);
		if(si!=null) {
			FluidStack fs= new FluidStack(si.getFluidType(),250);
			fs.applyComponents(stack.getComponentsPatch());
			fs.remove(CPCapability.ITEM_FLUID);
			return fs;
		}
		return Optional.ofNullable(stack.getCapability(FluidHandler.ITEM)).map(t->t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
	}
	public static Fluid getFluidType(ItemStack stack) {
		ItemHoldedFluidData si=stack.get(CPCapability.ITEM_FLUID);
		if(si!=null) {
			return si.getFluidType();
		}
		return Optional.ofNullable(stack.getCapability(FluidHandler.ITEM)).map(t->t.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
	}
	public static JsonElement toJson(Ingredient i) {
		return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE,i).result().orElse(JsonNull.INSTANCE);
	}
	public static ItemStack extractOutput(IItemHandler inv,int count) {
		ItemStack is=ItemStack.EMPTY;
		for(int i=0;i<inv.getSlots();i++) {
			is=inv.extractItem(i, count, false);
			if(!is.isEmpty())break;
		}
		return is;
	}
	public static boolean isExtractAllowed(ItemStack is) {
		FoodExchangeItemEvent ev=new FoodExchangeItemEvent.Pre(is);
		NeoForge.EVENT_BUS.post(ev);
		return ev.getResult()==EventResult.ALLOW;
	}
	public static boolean isExchangeAllowed(ItemStack or,ItemStack rs) {
		FoodExchangeItemEvent ev=new FoodExchangeItemEvent.Post(or,rs);
		NeoForge.EVENT_BUS.post(ev);
		return ev.getResult()==EventResult.ALLOW;
	}
	public static ItemStack insertToOutput(ItemStackHandler inv, int slot, ItemStack in) {
		ItemStack is = inv.getStackInSlot(slot);
		if (is.isEmpty()) {
			inv.setStackInSlot(slot, in.split(Math.min(inv.getSlotLimit(slot), in.getMaxStackSize())));
		} else if (ItemStack.isSameItemSameComponents(in, is)) {
			int limit = Math.min(inv.getSlotLimit(slot), is.getMaxStackSize());
			limit -= is.getCount();
			limit = Math.min(limit, in.getCount());
			is.grow(limit);
			in.shrink(limit);
		}
		return in;
	}
	public static void dropToWorld(Level level,ItemStack is,BlockPos pos) {
		if (!is.isEmpty() && !level.isClientSide)
        {
            ItemEntity entityitem = new ItemEntity(level, pos.getX(), pos.getY() + 0.5, pos.getZ(),is);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

            level.addFreshEntity(entityitem);
        }
	}
	public static MutableComponent translate(String format,Object...objects) {
		return translateWithFallback(format,null,objects);
	}
	public static MutableComponent translate(String format) {
		return translate(format,new Object[0]);
	}
	public static MutableComponent translateWithFallback(String format,String fallback,Object...objects) {
		return MutableComponent.create(new TranslatableContents(format,fallback,objects));
	}
	public static MutableComponent translateWithFallback(String format,String fallback) {
		return translate(format,fallback,new Object[0]);
	}
	public static MutableComponent string(String content) {
		return MutableComponent.create(PlainTextContents.create(content));
	}
	public static ResourceLocation getRegistryName(Fluid f) {
		return BuiltInRegistries.FLUID.getKey(f);
	}
	public static ResourceLocation getRegistryName(DeferredHolder<?,?> r) {
		return r.getId();
	}
	public static ResourceLocation getRegistryName(Item i) {
		return BuiltInRegistries.ITEM.getKey(i);
	}
	public static ResourceLocation getRegistryName(ItemStack i) {
		return getRegistryName(i.getItem());
	}
	public static ResourceLocation getRegistryName(Block b) {
		return BuiltInRegistries.BLOCK.getKey(b);
	}

	public static ResourceLocation getRegistryName(FluidStack f) {
		return getRegistryName(f.getFluid());
	}

	public static ResourceLocation getRegistryName(MobEffect effect) {
		return BuiltInRegistries.MOB_EFFECT.getKey(effect);
	}
	
	public static void addPotionTooltip(List<MobEffectInstance> list, Consumer<Component> lores, float durationFactor,Level pLevel) {
		PotionContents.addPotionTooltip(list, lores, durationFactor, pLevel == null ? 20.0F : pLevel.tickRateManager().tickrate());
	}
	public static void writeItemFluid(ItemStack is, Fluid f) {
		is.set(CPCapability.ITEM_FLUID, new ItemHoldedFluidData(f));
	}
	public static StewInfo getOrCreateInfo(ItemStack stack) {
		StewInfo si= stack.get(CPCapability.STEW_INFO);
		if(si==null) {
			Fluid type=Utils.getFluidType(stack);
			if(type==Fluids.EMPTY)
				return new StewInfo();
			else
				return new StewInfo(type);
		}
		return si;
	}
	public static StewInfo getOrCreateInfo(FluidStack stack) {
		StewInfo si= stack.get(CPCapability.STEW_INFO);
		if(si==null) {
			Fluid type=stack.getFluid();
			if(type==Fluids.EMPTY)
				return new StewInfo();
			else
				return new StewInfo(type);
		}
		return si;
	}
	public static void setInfo(MutableDataComponentHolder out, StewInfo info) {
		out.set(CPCapability.STEW_INFO, info);
		
	}
}
