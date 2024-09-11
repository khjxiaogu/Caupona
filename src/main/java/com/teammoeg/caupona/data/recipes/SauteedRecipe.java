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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPTags.Items;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.recipes.conditions.Conditions;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SauteedRecipe extends IDataRecipe implements IConditionalRecipe {


	public SauteedRecipe(List<IngredientCondition> allow, List<IngredientCondition> deny, int priority, int time, Item output, boolean removeNBT, float count, Ingredient bowl, ResourceLocation model) {
		super();
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.output = output;
		this.removeNBT = removeNBT;
		this.count = count;
		this.bowl = bowl;
		this.model=model;
	}

	public static Set<CookIngredients> cookables;
	public static List<RecipeHolder<SauteedRecipe>> sorted;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static boolean isCookable(ItemStack stack) {
		FloatemTagStack s = new FloatemTagStack(stack);
		return stack.is(Items.COOKABLE) || cookables.stream().anyMatch(e -> e.fits(s));
		// return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	List<IngredientCondition> allow;
	List<IngredientCondition> deny;
	int priority = 0;
	public int time;
	public Item output;
	public boolean removeNBT=false;
	public float count=2f;
	public Ingredient bowl;
	public ResourceLocation model;
	public static final MapCodec<SauteedRecipe> CODEC=
		RecordCodecBuilder.mapCodec(t->t.group(
			Codec.list(Conditions.CODEC).optionalFieldOf("allow").forGetter(o->Optional.ofNullable(o.allow)),
			Codec.list(Conditions.CODEC).optionalFieldOf("deny").forGetter(o->Optional.ofNullable(o.deny)),
			Codec.INT.fieldOf("priority").forGetter(o->o.priority),
			Codec.INT.fieldOf("time").forGetter(o->o.time),
			BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(o->o.output),
			Codec.BOOL.fieldOf("removeNBT").forGetter(o->o.removeNBT),
			Codec.FLOAT.fieldOf("ingredientPerDish").forGetter(o->o.count),
			Ingredient.CODEC.fieldOf("bowl").forGetter(o->o.bowl),
			ResourceLocation.CODEC.fieldOf("model").forGetter(o->o.model)
				).apply(t, SauteedRecipe::new));

/*
	public SauteedRecipe(FriendlyByteBuf data) {
		allow = SerializeUtil.readList(data, Conditions::of);
		deny = SerializeUtil.readList(data, Conditions::of);
		priority = data.readVarInt();
		time = data.readVarInt();
		output = data.readById(BuiltInRegistries.ITEM);
		removeNBT=data.readBoolean();
		count=data.readFloat();
	}*/
	public SauteedRecipe(Optional<List<IngredientCondition>> allow, Optional<List<IngredientCondition>> deny,
			int priority, int time, Item output,boolean removeNBT,float count,Ingredient bowl,ResourceLocation model) {
		this.allow = allow.orElse(null);
		this.deny = deny.orElse(null);
		this.priority = priority;
		this.time = time;
		this.output = output;
		this.removeNBT=removeNBT;
		this.count=count;
		this.bowl=bowl;
		this.model=model;
	}
/*
	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, allow, Conditions::write);
		SerializeUtil.writeList(data, deny, Conditions::write);
		data.writeVarInt(priority);
		data.writeVarInt(time);
		data.writeId(BuiltInRegistries.ITEM, output);
		data.writeBoolean(removeNBT);
		data.writeFloat(count);
	}
*/
	public boolean matches(PanPendingContext ctx) {
		if (allow != null)
			if (!allow.stream().allMatch(ctx::compute))
				return false;
		if (deny != null)
			if (deny.stream().anyMatch(ctx::compute))
				return false;
		return true;
	}

	public Stream<CookIngredients> getAllNumbers() {
		return Stream.concat(
				allow == null ? Stream.empty() : allow.stream().flatMap(IngredientCondition::getAllNumbers),
				deny == null ? Stream.empty() : deny.stream().flatMap(IngredientCondition::getAllNumbers));
	}

	public Stream<ResourceLocation> getTags() {
		return Stream.concat(allow == null ? Stream.empty() : allow.stream().flatMap(IngredientCondition::getTags),
				deny == null ? Stream.empty() : deny.stream().flatMap(IngredientCondition::getTags));
	}

	public int getPriority() {
		return priority;
	}

	public List<IngredientCondition> getAllow() {
		return allow;
	}

	public List<IngredientCondition> getDeny() {
		return deny;
	}

}
