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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.item.DishItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SauteedFoodInfo extends SpicedFoodInfo implements IFoodInfo{
	public static final Codec<SauteedFoodInfo> CODEC=RecordCodecBuilder.create(o->codecStart(o)
		.and(o.group(Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(i->i.stacks),
			Codec.list(PossibleEffect.CODEC).fieldOf("effects").forGetter(i->i.foodeffect),
			Codec.INT.fieldOf("heal").forGetter(i->i.healing),
			Codec.FLOAT.fieldOf("sat").forGetter(i->i.saturation))
		).apply(o, SauteedFoodInfo::new));
	public List<FloatemStack> stacks;
	public List<FoodProperties.PossibleEffect> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	
	public SauteedFoodInfo(Optional<MobEffectInstance> spice, Boolean hasSpice, Optional<ResourceLocation> spiceName, List<FloatemStack> stacks, List<PossibleEffect> foodeffect, int healing,
		float saturation) {
		super(spice, hasSpice, spiceName);
		this.stacks = new ArrayList<>(stacks);
		this.foodeffect = new ArrayList<>(foodeffect);
		this.healing = healing;
		this.saturation = saturation;
	}

	public SauteedFoodInfo(List<FloatemStack> stacks, int healing, float saturation) {
		super();
		this.stacks = stacks;
		this.healing = healing;
		this.saturation = saturation;
	}

	public SauteedFoodInfo() {
		this(new ArrayList<>(), 0, 0);
	}


	public boolean isEmpty() {
		return stacks.isEmpty();
	}

	public void completeAll() {
		completeData();
	}

	public void completeData() {
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.stack.getItem())));
		
		foodeffect.sort(
				Comparator.<FoodProperties.PossibleEffect,String>comparing(e -> e.effect().getEffect().getRegisteredName())
						.thenComparing(e->e.probability()));
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void recalculateHAS() {
		foodeffect.clear();
		float nh = 0;
		float ns = 0;
		for (FloatemStack fs : stacks) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(fs.getItem());
			if (fvr != null) {
				nh += fvr.heal * fs.count;
				ns += fvr.sat * fs.count * fvr.heal;
				if(fvr.effects!=null)
					foodeffect.addAll(fvr.effects);
				continue;
			}
			FoodProperties f = fs.getStack().getFoodProperties(null);
			if (f != null) {
				nh += fs.count * f.nutrition();
				ns += fs.count * f.saturation()* f.nutrition();
				foodeffect.addAll(f.effects());
			}
		}
		int conv = (int) (0.075 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.6f, ns / this.healing);
		else
			this.saturation =0;
	}

	public void setParts(int parts) {
		for (FloatemStack i : stacks) {
			i.count/=parts;
		}
	}
	public void addItem(ItemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount();
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount()));
	}

	public void addItem(FloatemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is.getStack())) {
				i.count += is.count;
				return;
			}
		}
		stacks.add(is);
	}

	@Override
	public List<FloatemStack> getStacks() {
		return stacks;
	}

	public int getHealing() {
		return healing;
	}

	public float getSaturation() {
		return saturation;
	}
	public FoodProperties getFood() {
		
		FoodProperties.Builder b = new FoodProperties.Builder();

		if (spice != null)
			b.effect(()->new MobEffectInstance(spice), 1);
		for (PossibleEffect ef : foodeffect) {
			b.effect(()->ef.effect(), ef.probability());
		}
		b.nutrition(healing);
		if(Float.isNaN(saturation))
			b.saturationModifier(0f);
		else
			b.saturationModifier(saturation);
		return b.build();
	}

	@Override
	public List<PossibleEffect> getEffects() {
		List<PossibleEffect> li=new ArrayList<>();
		if (spice != null)
			li.add(new PossibleEffect(()->new MobEffectInstance(spice), 1f));
		for (PossibleEffect ef : foodeffect) {
			li.add(new PossibleEffect(()->ef.effect(), ef.probability()));
		}
		return null;
	}

}
