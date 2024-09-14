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

package com.teammoeg.caupona.data.recipes.baseconditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class FluidTypeType implements StewBaseCondition {
	Fluid of;
	public static final MapCodec<FluidTypeType> CODEC=RecordCodecBuilder
		.mapCodec(c->c.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("base").forGetter(t->t.of)
		).apply(c, FluidTypeType::new));
	/*public FluidTypeType(JsonObject jo) {
		of = ResourceLocation.parse(jo.get("base").getAsString());
	}*/
	/*public FluidTypeType(String of) {
		this(ResourceLocation.parse(of));
	}*/
	public FluidTypeType(Fluid of) {
		super();
		this.of = of;
	}
/*
	public FluidTypeType(Fluid of) {
		super();
		this.of = Utils.getRegistryName(of);
	}
*/
	@Override
	public Integer apply(Fluid t, Fluid u) {
		return test(u) ? 2 : 0;
	}
	@Override
	public boolean test(Fluid t) {
		return of.equals(t);
	}

/*
	public JsonObject serialize() {
		JsonObject jo = new JsonObject();
		jo.addProperty("fluid_type", of.toString());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(of);
	}

	public FluidTypeType(FriendlyByteBuf buffer) {
		of = buffer.readResourceLocation();
	}
*/
	@Override
	public String getType() {
		return "fluid_type";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((of == null) ? 0 : of.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FluidTypeType))
			return false;
		FluidTypeType other = (FluidTypeType) obj;
		if (of == null) {
			if (other.of != null)
				return false;
		} else if (!of.equals(other.of))
			return false;
		return true;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation(of.getFluidType().getDescriptionId());
	}

}
