package com.teammoeg.caupona.components;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class ImmutableStewInfo extends StewInfo {

	public ImmutableStewInfo(Optional<MobEffectInstance> spice, Boolean hasSpice, Optional<ResourceLocation> spiceName, List<FloatemStack> stacks, List<MobEffectInstance> effects,
		List<ChancedEffect> foodeffect, int healing, float saturation, Fluid base) {
		super(spice, hasSpice, spiceName, stacks, effects, foodeffect, healing, saturation, base);
	}
	@Override
	public void setBase(Fluid base) {
		throw new UnsupportedOperationException();
	}
	public ImmutableStewInfo(StewInfo info) {
		super(info.spice,info.hasSpice, info.spiceName,ImmutableList.copyOf(info.stacks), ImmutableList.copyOf(info.effects), ImmutableList.copyOf(info.foodeffect), info.healing, info.saturation, info.base);
	}
	@Override
	public ImmutableStewInfo toImmutable() {
		return this;
	}
	@Override
	public boolean merge(StewInfo f, float cparts, float oparts) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void forceMerge(StewInfo f, float cparts, float oparts) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addEffect(MobEffectInstance eff, float parts) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void adjustParts(float oparts, float parts) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addItem(ItemStack is, float parts) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addItem(FloatemStack is) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addSpice(MobEffectInstance spice, ItemStack im) {
		throw new UnsupportedOperationException();
	}
	
}
