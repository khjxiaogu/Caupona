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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FoodValueRecipe extends IDataRecipe {
	public static Map<Item, FoodValueRecipe> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static Set<FoodValueRecipe> recipeset;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public int heal;
	public float sat;
	public List<FoodProperties.PossibleEffect> effects;
	public final Map<Item, Integer> processtimes;
	private ItemStack repersent;
	public transient Set<ResourceLocation> tags;
	public static final MapCodec<FoodValueRecipe> CODEC=
		RecordCodecBuilder.mapCodec(t->t.group(
			Codec.INT.fieldOf("heal").forGetter(o->o.heal),
			Codec.FLOAT.fieldOf("sat").forGetter(o->o.sat),
			
			Codec.optionalField("effects",Codec.list(FoodProperties.PossibleEffect.CODEC),false).forGetter(o->Optional.ofNullable(o.effects)),
			Codec.list(Utils.pairCodec("item",BuiltInRegistries.ITEM.byNameCodec(), "time", Codec.INT)).fieldOf("items").forGetter(o->o.getProcessTime()),
			Ingredient.CODEC.fieldOf("item").forGetter(o->Ingredient.of(o.repersent))
				).apply(t, FoodValueRecipe::new));
	public FoodValueRecipe(int heal, float sat,Optional<List<FoodProperties.PossibleEffect>> effects, List<Pair<Item, Integer>> processtimes, Ingredient repersent) {
		super();
		this.heal = heal;
		this.sat = sat;
		this.effects = effects.orElse(null);
		this.processtimes = new HashMap<>();
		for(Pair<Item, Integer> i:processtimes) {
			this.processtimes.put(i.getFirst(), i.getSecond());
		}
		if(!repersent.isEmpty())
		this.repersent = repersent.getItems()[0];
	}
	public List<Pair<Item, Integer>> getProcessTime(){
		return processtimes.entrySet().stream().map(t->Pair.of(t.getKey(),t.getValue())).toList();
	}
	public FoodValueRecipe(int heal, float sat, ItemStack rps, Item... types) {
		this.heal = heal;
		this.sat = sat;
		processtimes = new LinkedHashMap<>();
		repersent = rps;
		for (Item i : types)
			processtimes.put(i, 0);
	}/*
	public FoodValueRecipe( FriendlyByteBuf data) {
		heal = data.readVarInt();
		sat = data.readFloat();
		processtimes = SerializeUtil.readList(data, d -> new Pair<>(d.readById(BuiltInRegistries.ITEM), d.readVarInt())).stream()
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		effects = SerializeUtil.readList(data, d -> new Pair<>(MobEffectInstance.load(d.readNbt()), d.readFloat()));
		repersent = SerializeUtil.readOptional(data, d -> ItemStack.of(d.readNbt())).orElse(null);
	}

	public void write(FriendlyByteBuf data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
		SerializeUtil.writeList2(data, processtimes.entrySet(), (d, e) -> {
			d.writeId(BuiltInRegistries.ITEM,e.getKey());

			d.writeVarInt(e.getValue());
		});
		SerializeUtil.writeList2(data, effects, (d, e) -> {
			CompoundTag nc = new CompoundTag();
			e.getFirst().save(nc);
			d.writeNbt(nc);
			d.writeFloat(e.getSecond());
		});
		SerializeUtil.writeOptional(data, repersent, (d, e) -> e.writeNbt(d.save(new CompoundTag())));
	}
*/
	public void clearCache() {
		tags = null;
	}

	public Set<ResourceLocation> getTags() {
	
		if (tags == null)
			tags = processtimes.keySet().stream()
					.flatMap(i -> BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getId(i)).map(Holder<Item>::tags).orElseGet(Stream::empty).map(TagKey::location))
					.filter(CountingTags.tags::contains).collect(Collectors.toSet());
		return tags;
	}

	public ItemStack getRepersent() {
		return repersent;
	}

	public void setRepersent(ItemStack repersent) {
		if (repersent != null)
			this.repersent = repersent.copy();
		else
			this.repersent = null;
	}


}
