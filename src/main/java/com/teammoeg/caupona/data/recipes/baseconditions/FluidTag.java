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

package com.teammoeg.caupona.data.recipes.baseconditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidTag implements StewBaseCondition {
	TagKey<Fluid> f;
	public static final MapCodec<FluidTag> CODEC=RecordCodecBuilder
		.mapCodec(c->c.group(ResourceLocation.CODEC.fieldOf("tag").forGetter(t->t.f.location())
		).apply(c, FluidTag::new));
	/*public FluidTag(JsonObject jo) {
		f = FluidTags.create(ResourceLocation.parse(jo.get("tag").getAsString()));
	}*/

	public FluidTag(ResourceLocation tag) {
		super();
		f = FluidTags.create(tag);
	}
	public FluidTag(TagKey<Fluid> tag) {
		super();
		f = tag;
	}
	@Override
	public Integer apply(Fluid t, Fluid u) {
		return test(u) ? 2 : test(t) ? 1 : 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean test(Fluid t) {
		return t.is(this.f);
	}
/*
	public JsonObject serialize() {
		JsonObject jo = new JsonObject();
		jo.addProperty("tag", f.location().toString());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(f.location());
	}
*/
	public FluidTag(FriendlyByteBuf buffer) {
		f = FluidTags.create(buffer.readResourceLocation());
	}

	@Override
	public String getType() {
		return "tag";
	}



	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("tag." + f.location().toLanguageKey().replaceAll("/", "."));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FluidTag other = (FluidTag) obj;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		return true;
	}

}
