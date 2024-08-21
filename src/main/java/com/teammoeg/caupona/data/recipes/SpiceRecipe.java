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

package com.teammoeg.caupona.data.recipes;

import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SpiceRecipe extends IDataRecipe {
	public static List<SpiceRecipe> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public Ingredient spice;
	public MobEffectInstance effect;
	public boolean canReactLead=false;
	public static final Codec<SpiceRecipe> CODEC=
			RecordCodecBuilder.create(t->t.group(
					Ingredient.CODEC_NONEMPTY.fieldOf("spice").forGetter(o->o.spice),
					MobEffectInstance.CODEC.fieldOf("effect").forGetter(o->o.effect),
					Codec.BOOL.fieldOf("reacts_lead").forGetter(o->o.canReactLead)
					).apply(t, SpiceRecipe::new));
	/*public SpiceRecipe(JsonObject jo) {

		spice = Ingredient.fromJson(jo.get("spice"),true);
		if (jo.has("effect")) {
			JsonObject x = jo.get("effect").getAsJsonObject();
			int amplifier = 0;
			if (x.has("level"))
				amplifier = x.get("level").getAsInt();
			int duration = 0;
			if (x.has("time"))
				duration = x.get("time").getAsInt();
			MobEffect eff = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(x.get("effect").getAsString()));
			if (eff != null)
				effect = new MobEffectInstance(eff, duration, amplifier);
		}
		if(jo.has("reacts_lead"))
			canReactLead=jo.get("reacts_lead").getAsBoolean();
	}

	public SpiceRecipe(FriendlyByteBuf pb) {
		spice = Ingredient.fromNetwork(pb);

		effect = SerializeUtil.readOptional(pb, b -> MobEffectInstance.load(b.readNbt())).orElse(null);
		canReactLead=pb.readBoolean();
	}*/

	public SpiceRecipe(Ingredient spice, MobEffectInstance effect) {
		this.spice = spice;
		this.effect = effect;
	}

	public SpiceRecipe(Ingredient spice, MobEffectInstance effect, boolean canReactLead) {
		this.spice = spice;
		this.effect = effect;
		this.canReactLead = canReactLead;
	}
/*
	public void write(FriendlyByteBuf pack) {
		spice.toNetwork(pack);
		SerializeUtil.writeOptional(pack, effect, (e, b) -> b.writeNbt(e.save(new CompoundTag())));
		pack.writeBoolean(canReactLead);
	}
*/
/*	public void serializeRecipeData(JsonObject jx) {
		jx.add("spice", Utils.toJson(spice));
		if (effect != null) {
			JsonObject jo = new JsonObject();
			jo.addProperty("level", effect.getAmplifier());
			jo.addProperty("time", effect.getDuration());
			jo.addProperty("effect", Utils.getRegistryName(effect.getEffect()).toString());
			jx.add("effect", jo);
		}
		jx.addProperty("reacts_lead",canReactLead);
	}
*/
	public static int getMaxUse(ItemStack spice) {
		return spice.getMaxDamage() - spice.getDamageValue();
	}

	public static ItemStack handle(ItemStack spice, int cnt) {
		int cdmg = spice.getDamageValue();
		cdmg += cnt;
		if (cdmg >= spice.getMaxDamage()) {
			return spice.getCraftingRemainingItem();
		}
		spice.setDamageValue(cdmg);
		return spice;
	}

	public static SpiceRecipe find(ItemStack spice) {
		return recipes.stream().filter(e -> e.spice.test(spice)).findFirst().orElse(null);
	}

	public static boolean isValid(ItemStack spice) {
		return recipes.stream().anyMatch(e -> e.spice.test(spice));
	}

}
