package com.teammoeg.caupona;

import com.teammoeg.caupona.effects.HyperactiveMobEffect;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(Registries.MOB_EFFECT, CPMain.MODID);
	public static final DeferredHolder<MobEffect,MobEffect> HYPERACTIVE=EFFECTS.register("hyperactive",
			()->new HyperactiveMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f, 3.0D)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "hyperactive"), 0.0D, AttributeModifier.Operation.ADD_VALUE)
			);
}
