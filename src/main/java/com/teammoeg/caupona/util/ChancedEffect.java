package com.teammoeg.caupona.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;

public class ChancedEffect implements Comparable<ChancedEffect>{
	public final static Codec<ChancedEffect> CODEC=RecordCodecBuilder.create(t->t.group(
		SerializeUtil.fromRFBBStreamCodec(MobEffectInstance.STREAM_CODEC,MobEffectInstance.CODEC).fieldOf("effect").forGetter(o->o.effect),
		Codec.FLOAT.fieldOf("chance").forGetter(o->o.chance)
		).apply(t, ChancedEffect::new));
	public MobEffectInstance effect;
	public float chance;
	public ChancedEffect(MobEffectInstance effect, float chance) {
		super();
		this.effect = effect;
		this.chance = chance;
	}
	public static ChancedEffect createByParts(MobEffectInstance effect, float parts) {
		effect=new MobEffectInstance(effect);
		if(BuiltInRegistries.MOB_EFFECT.get(effect.getEffect().getKey()).isInstantenous()||effect.duration<parts) {
			return new ChancedEffect(effect,1f/parts);
		}
		effect.duration=(int) (effect.duration/parts);
		return new ChancedEffect(effect,1);
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
/*	public PossibleEffect toPossibleEffect() {
		return new PossibleEffect(effectSupplier(),chance);
	}*/
	public void toPossibleEffects(Consumer<PossibleEffect> consumer) {
		if(chance<=1)
			consumer.accept(new PossibleEffect(effectSupplier(),chance));
		else {
			consumer.accept(new PossibleEffect(effectSupplier(),1));
			consumer.accept(new PossibleEffect(effectSupplier(),chance-1));
		}
	}
	public void toPossibleEffects(FoodProperties.Builder builder) {
		if(chance<=1)
			builder.effect(effectSupplier(),chance);
		else {
			builder.effect(effectSupplier(),1);
			builder.effect(effectSupplier(),chance-1);
		}
	}
	
	public boolean merge(ChancedEffect other,float otherCount,float thisCount) {
		if(isEffectEquals(this.effect,other.effect)) {
			if(this.effect.equals(other.effect)||BuiltInRegistries.MOB_EFFECT.get(this.effect.getEffect().getKey()).isInstantenous()) {
				this.chance+=other.chance* otherCount / thisCount;
				return true;
			}else{
				this.effect.duration=(int) ((this.effect.duration*this.chance+other.effect.duration*other.chance* otherCount / thisCount)/this.chance);
				return true;
			}
		}
		return false;
		
	}
	public boolean add(MobEffectInstance other,float parts) {
		if (isEffectEquals(effect, other)) {
			if((this.effect.equals(other)&&this.chance+1f/parts<=1)||BuiltInRegistries.MOB_EFFECT.get(this.effect.getEffect().getKey()).isInstantenous()) {
				this.chance+=1f/parts;
				return true;
			}else{
				effect.duration = Math.max(other.duration,(int)Math.min(effect.duration + other.duration / parts, other.duration * 2f));
				return true;
			}
			
		}

		return false;
		
	}
	public void adjustParts(float otherCount,float thisCount) {
		if(BuiltInRegistries.MOB_EFFECT.get(this.effect.getEffect().getKey()).isInstantenous()||this.effect.duration<thisCount/otherCount) {
			this.chance*=otherCount / thisCount;
		}else{
			this.effect.duration*= otherCount / thisCount;
		}
		
	}
	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}
	@Override
	public int compareTo(ChancedEffect o) {
		int rslt=effect.getEffect().getRegisteredName().compareTo(o.effect.getEffect().getRegisteredName());
		if(rslt!=0)
			return rslt;
		rslt=Float.compare(chance, o.chance);
		return rslt;
	}
}
