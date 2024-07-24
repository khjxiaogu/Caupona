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
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.item.StewItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class StewInfo extends SpicedFoodInfo implements IFoodInfo {
	public static Codec<StewInfo> CODEC;
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<PossibleEffect> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	public float shrinkedFluid = 0;
	public ResourceLocation base;
	public StewInfo(List<FloatemStack> stacks, List<MobEffectInstance> effects, int healing, float saturation,
			ResourceLocation base) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.healing = healing;
		this.saturation = saturation;
		this.base = base;
	}

	public StewInfo() {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, ResourceLocation.withDefaultNamespace("water"));
	}
	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public boolean canAlwaysEat() {
		return healing <= 1 || getDensity() <= 0.5;
	}

	public boolean isEmpty() {
		return stacks.isEmpty() && effects.isEmpty();
	}

	public boolean canMerge(StewInfo f, float cparts, float oparts) {
		return (this.getDensity() * cparts + f.getDensity() * oparts) / (cparts + oparts) <= 3;
	}

	public boolean merge(StewInfo f, float cparts, float oparts) {
		if (!canMerge(f, cparts, oparts))
			return false;
		forceMerge(f, cparts, oparts);
		return true;
	}

	public void forceMerge(StewInfo f, float cparts, float oparts) {

		for (MobEffectInstance es : f.effects) {
			boolean added = false;
			for (MobEffectInstance oes : effects) {
				if (isEffectEquals(oes, es)) {
					oes.duration += es.duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3) {
					MobEffectInstance copy=new MobEffectInstance(es);
					copy.duration=(int) (copy.duration*oparts/cparts);
					effects.add(copy);
				}
			}
		}
		for (PossibleEffect es : f.foodeffect) {
			boolean added = false;
			for (PossibleEffect oes : foodeffect) {
				if (es.probability() == oes.probability() && isEffectEquals(oes.effect(), es.effect())) {
					oes.effect().duration += es.effect().duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				foodeffect.add(es);
			}
		}
		shrinkedFluid += f.shrinkedFluid * oparts / cparts;
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.count * oparts / cparts));
		}
		completeAll();
	}

	public void completeAll() {
		clearSpice();
		completeData();
		completeEffects();
	}

	public void completeData() {
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.stack.getItem())));
		foodeffect.sort(
				Comparator.<PossibleEffect,String>comparing(e -> e.effect().getEffect().getRegisteredName())
						.thenComparing(e->e.probability()));
	}

	public void completeEffects() {
		effects.sort(Comparator.<MobEffectInstance,String>comparing(e -> e.getEffect().getRegisteredName())
				.thenComparingInt(e -> e.getDuration()));
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(MobEffectInstance eff, float parts) {

		for (MobEffectInstance oes : effects) {
			if (isEffectEquals(oes, eff)) {
				oes.duration = Math.max(oes.duration,
						(int) Math.min(oes.duration + eff.duration / parts, eff.duration * 2f));
				return;
			}
		}
		if (effects.size() < 3) {
			MobEffectInstance copy = new MobEffectInstance(eff);
			copy.duration /= parts;
			effects.add(copy);
		}
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
				ns += fs.count * f.saturation() * f.nutrition();
				foodeffect.addAll(f.effects());
			}
		}
		RecipeHolder<FluidFoodValueRecipe> ffvr = FluidFoodValueRecipe.recipes.get(this.base);
		if (ffvr != null) {
			nh += ffvr.value().heal * (1 + this.shrinkedFluid);
			ns += ffvr.value().sat * (1 + this.shrinkedFluid);
		}
		float dense = this.getDensity();
		/*
		 * if(nh>0) {
		 * nh+=Mth.clamp(dense,1,2);
		 * }
		 */
		int conv = (int) (Mth.clamp((dense - 1) / 2f, 0, 1) * 0.3 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.7f, ns / this.healing);
		else
			this.saturation=0;
	}

	public void adjustParts(float oparts, float parts) {
		if (oparts == parts)
			return;
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}

		for (MobEffectInstance es : effects) {
			es.duration = (int) (es.duration * oparts / parts);
		}
		for (PossibleEffect es : foodeffect) {
			es.effect().duration = (int) (es.effect().duration * oparts / parts);
		}
		float delta = 0;
		if (oparts > parts)
			delta = oparts - parts;
		clearSpice();
		shrinkedFluid = (shrinkedFluid * oparts + delta) / parts;
		healing = (int) (healing * oparts / parts);
		
		saturation = saturation * oparts / parts;
	}

	public StewInfo(ResourceLocation base) {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, base);
	}



	public static String getRegName(CompoundTag nbt) {
		return nbt.getString("base");
	}

	public void addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount() / parts;
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount() / parts));
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

	public void write(CompoundTag nbt) {
		super.write(nbt);
		nbt.put("items", SerializeUtil.toNBTList(stacks, FloatemStack::serializeNBT));
		nbt.put("effects", SerializeUtil.toNBTList(effects, e -> e.save(new CompoundTag())));
		nbt.put("feffects", SerializeUtil.toNBTList(foodeffect, e -> {
			CompoundTag cnbt = new CompoundTag();
			cnbt.put("effect", e.getFirst().save(new CompoundTag()));
			cnbt.putFloat("chance", e.getSecond());
			return cnbt;
		}));
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
		nbt.putString("base", base.toString());
		nbt.putFloat("afluid", shrinkedFluid);
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

	@SuppressWarnings("deprecation")
	public FoodProperties getFood() {

		FoodProperties.Builder b = new FoodProperties.Builder();
		for (MobEffectInstance eff : effects) {
			if (eff != null) {
				b.effect(eff, 1);
			}
		}
		if (spice != null)
			b.effect(()->new MobEffectInstance(spice), 1);
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			b.effect(()->new MobEffectInstance(ef.getFirst()), ef.getSecond());
		}
		b.nutrition(healing);
		if(Float.isNaN(saturation))
			b.saturationModifier(0);
		else
			b.saturationModifier(saturation);
		if (canAlwaysEat())
			b.alwaysEdible();
		return b.build();
	}
	@Override
	public List<Pair<Supplier<MobEffectInstance>, Float>> getEffects() {
		List<Pair<Supplier<MobEffectInstance>, Float>> li=new ArrayList<>();
		for (MobEffectInstance eff : effects) {
			if (eff != null) {
				li.add(Pair.of(()->new MobEffectInstance(eff), 1f));
			}
		}
		if (spice != null)
			li.add(Pair.of(()->new MobEffectInstance(spice), 1f));
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			li.add(Pair.of(()->new MobEffectInstance(ef.getFirst()), ef.getSecond()));
		}
		return null;
	}


	@Override
	public void read(CompoundTag nbt) {
		super.read(nbt);
		stacks = nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
			.collect(Collectors.toList());
		effects = nbt.getList("effects", 10).stream().map(e -> (CompoundTag) e).map(MobEffectInstance::load)
				.collect(Collectors.toList());
		healing = nbt.getInt("heal");
		saturation = nbt.getFloat("sat");
		foodeffect = nbt.getList("feffects", 10).stream().map(e -> (CompoundTag) e)
				.map(e -> new Pair<>(MobEffectInstance.load(e.getCompound("effect")), e.getFloat("chance")))
				.collect(Collectors.toList());
		base = new ResourceLocation(nbt.getString("base"));
	
		shrinkedFluid = nbt.getFloat("afluid");
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(base, effects, foodeffect, healing, saturation, shrinkedFluid, stacks);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		StewInfo other = (StewInfo) obj;
		return Objects.equals(base, other.base) && Objects.equals(effects, other.effects) && Objects.equals(foodeffect, other.foodeffect) && healing == other.healing
			&& Float.floatToIntBits(saturation) == Float.floatToIntBits(other.saturation) && Float.floatToIntBits(shrinkedFluid) == Float.floatToIntBits(other.shrinkedFluid)
			&& Objects.equals(stacks, other.stacks);
	}


}
