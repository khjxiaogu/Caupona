package com.teammoeg.caupona.util;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties.PossibleEffect;

public class ChancedEffect {
	public final static Codec<ChancedEffect> CODEC=RecordCodecBuilder.create(t->t.group(
		MobEffectInstance.CODEC.fieldOf("effect").forGetter(o->o.effect),
		Codec.FLOAT.fieldOf("chance").forGetter(o->o.chance)
		).apply(t, ChancedEffect::new));
	public MobEffectInstance effect;
	public float chance;
	public ChancedEffect(MobEffectInstance effect, float chance) {
		super();
		this.effect = effect;
		this.chance = chance;
	}
	public ChancedEffect(PossibleEffect eff) {
		this(eff.effect(),eff.probability());
	}
	public Supplier<MobEffectInstance> effectSupplier(){
		return ()->new MobEffectInstance(effect);
	}
	public ChancedEffect copy() {
		return new ChancedEffect(new MobEffectInstance(effect),chance);
	}
	public boolean merge(ChancedEffect other,float otherCount,float thisCount) {
		if(isEffectEquals(this.effect,other.effect)) {
			if(this.effect.equals(other.effect)||BuiltInRegistries.MOB_EFFECT.get(this.effect.getEffect().getKey()).isInstantenous()) {
				this.chance+=other.chance* otherCount / thisCount;
			}else{
				this.effect.duration=(int) ((this.effect.duration*this.chance+other.effect.duration*other.chance* otherCount / thisCount)/this.chance);
			}
		}
		return false;
		
	}
	public void adjustParts(float otherCount,float thisCount) {
		if(BuiltInRegistries.MOB_EFFECT.get(this.effect.getEffect().getKey()).isInstantenous()) {
			this.chance*=otherCount / thisCount;
		}else{
			this.effect.duration*= otherCount / thisCount;
		}
		
	}
	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}
}
