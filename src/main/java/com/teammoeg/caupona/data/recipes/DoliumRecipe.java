/*
 * Copyright (c) 2024 TeamMoeg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DoliumRecipe extends IDataRecipe implements TimedRecipe{
	public static List<RecipeHolder<DoliumRecipe>> recipes;
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

	public List<SizedOrCatalystIngredient> items;
	public Ingredient extra;
	public Fluid base;
	public SizedOrCatalystFluidIngredient fluid ;
	public float density = 0;
	public boolean keepInfo = false;
	public ItemStack output;
	public int time;

	public DoliumRecipe(Fluid base, Fluid fluid, int amount, float density,
			boolean keep, ItemStack out, List<SizedOrCatalystIngredient> items, int time) {
		this( base, fluid, amount, density, keep, out, items, null,time);
	}

	public DoliumRecipe(List<SizedOrCatalystIngredient> items, Optional<Ingredient> extra, Optional<Fluid> base, Optional<SizedOrCatalystFluidIngredient> fluid,
			 float density, boolean keepInfo, ItemStack output,int time) {
		super();
		this.items = items;
		this.extra = extra.orElse(null);
		this.base = base.orElse(null);
		this.fluid = fluid.orElse(null);
		this.density = density;
		this.keepInfo = keepInfo;
		this.output = output;
		this.time=time;
	}

	public DoliumRecipe(Fluid base, Fluid fluid, int amount, float density,
			boolean keep, ItemStack out, Collection<SizedOrCatalystIngredient> items, Ingredient ext,int time) {
		if (items != null)
			this.items = new ArrayList<>(items);
		else
			this.items = new ArrayList<>();

		this.base = base;
		if(fluid!=Fluids.EMPTY)
			this.fluid = SizedOrCatalystFluidIngredient.of(fluid,amount);
		this.density = density;
		this.output = out;
		this.extra = ext;
		keepInfo = keep;
		this.time=time;
	}
	public static final MapCodec<DoliumRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					Codec.list(SizedOrCatalystIngredient.FLAT_CODEC).fieldOf("items").forGetter(o->o.items),
					Ingredient.CODEC.optionalFieldOf("container").forGetter(o->Optional.ofNullable(o.extra)),
					BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("base").forGetter(o->Optional.ofNullable(o.base)),
					SizedOrCatalystFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(o->Optional.ofNullable(o.fluid)),
					Codec.FLOAT.fieldOf("density").forGetter(o->o.density),
					Codec.BOOL.fieldOf("keepInfo").forGetter(o->o.keepInfo),
					ItemStack.CODEC.fieldOf("output").forGetter(o->o.output),
					Codec.INT.optionalFieldOf("time", 1200).forGetter(o->o.time)
					).apply(t, DoliumRecipe::new));

	public static DoliumRecipe testPot(FluidStack fluidStack) {
		return recipes.stream().map(t->t.value()).filter(t -> t.test(fluidStack, ItemStack.EMPTY)).findFirst().orElse(null);
	}

	public static boolean testInput(ItemStack stack) {
		return recipes.stream().map(t->t.value()).anyMatch(t -> t.items.stream().anyMatch(i -> i.test(stack)));
	}

	public static boolean testContainer(ItemStack stack) {
		return recipes.stream().map(t->t.value()).map(t -> t.extra).filter(Objects::nonNull).anyMatch(t -> t.test(stack));
	}

	public static RecipeHolder<DoliumRecipe> testDolium(FluidStack f, ItemStackHandler inv) {
		ItemStack is0 = inv.getStackInSlot(0);
		ItemStack is1 = inv.getStackInSlot(1);
		ItemStack is2 = inv.getStackInSlot(2);
		ItemStack cont = inv.getStackInSlot(4);
		return recipes.stream().filter(t -> t.value().test(f, cont, is0, is1, is2)).findFirst().orElse(null);
	}

	public boolean test(FluidStack f, ItemStack container, ItemStack... ss) {
		if (items.size() > 0) {
			if (ss.length < items.size())
				return false;
			int notEmpty = 0;
			for (ItemStack is : ss)
				if (!is.isEmpty())
					notEmpty++;
			if (notEmpty < items.size())
				return false;
		}
		if (extra != null && !extra.test(container))
			return false;
		if(fluid!=null&&!fluid.test(f))
			return false;

		if (density != 0 || base != null) {
			Optional<IFoodInfo> opinfo = CauponaHooks.getInfo(f);
			if(opinfo.isEmpty())
				return false;
			IFoodInfo info=opinfo.get();
			if (base != null && base!=info.getBase())
				return false;
			if (info.getDensity() < density)
				return false;
		}
		for (SizedOrCatalystIngredient igd : items) {
			boolean flag = false;
			for (ItemStack is : ss) {
				if (igd.test(is) ) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return true;
	}

	public ItemStack handle(FluidStack f) {
		int times = 1;
		if (fluid.amount() > 0)
			times = f.getAmount() / fluid.amount();
		ItemStack out = output.copy();
		out.setCount(out.getCount() * times);
		if (keepInfo) {
			StewInfo info = Utils.getOrCreateInfoForRead(f);
			Utils.setInfo(out, info);
		}
		f.shrink(times * fluid.amount());
		return out;
	}

	public ItemStack handleDolium(FluidStack f, ItemStackHandler inv) {
		int times = output.getMaxStackSize();
		if (fluid!=null&&fluid.amount() > 0)
			times = Math.min(f.getAmount() / fluid.amount(), times);
		if (extra != null)
			times = Math.min(times, inv.getStackInSlot(4).getCount());
		for (SizedOrCatalystIngredient igd : items) {
			if (igd.count() == 0)
				continue;
			for (int i = 0; i < 3; i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (igd.test(is)) {
					times = Math.min(times, is.getCount() / igd.count());
					break;
				}
			}
		}

		if (extra != null)
			inv.getStackInSlot(4).shrink(times);
		for (SizedOrCatalystIngredient igd : items) {
			if (igd.count() == 0)
				continue;
			for (int i = 0; i < 3; i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (igd.test(is)) {
					is.shrink(times * igd.count());
					break;
				}
			}
		}
		ItemStack out = output.copy();
		out.setCount(out.getCount() * times);
		if (keepInfo) {
			StewInfo info = Utils.getOrCreateInfoForRead(f);
			Utils.setInfo(out, info);
		}
		if (fluid!=null&&fluid.amount() > 0)
			f.shrink(times * fluid.amount());
		return out;
	}
/*
	public DoliumRecipe(FriendlyByteBuf data) {
		items = SerializeUtil.readList(data, d -> Pair.of(Ingredient.fromNetwork(d), d.readVarInt()));
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readResourceLocation).orElse(null);
		fluid = data.readById(BuiltInRegistries.FLUID);
		amount = data.readVarInt();
		density = data.readFloat();
		keepInfo = data.readBoolean();
		output = data.readItem();
		extra = SerializeUtil.readOptional(data, Ingredient::fromNetwork).orElse(null);
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items, (r, d) -> {
			r.getFirst().toNetwork(data);
			data.writeVarInt(r.getSecond());
		});
		SerializeUtil.writeOptional2(data, base, FriendlyByteBuf::writeResourceLocation);
		data.writeId(BuiltInRegistries.FLUID, fluid);
		data.writeVarInt(amount);
		data.writeFloat(density);
		data.writeBoolean(keepInfo);
		data.writeItem(output);
		SerializeUtil.writeOptional(data, extra, Ingredient::toNetwork);
	}
*/

	@Override
	public int getTime() {
		return time;
	}

}
