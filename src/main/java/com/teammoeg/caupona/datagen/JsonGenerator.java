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

package com.teammoeg.caupona.datagen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class JsonGenerator implements DataProvider {
	protected final PackOutput output;
	protected ExistingFileHelper helper;
	private String name;
	PackType type;
	public JsonGenerator(PackType pt,PackOutput output, ExistingFileHelper helper, String name) {
		super();
		this.type=pt;
		this.output = output;
		this.helper = helper;
		this.name = name;
	}
	protected abstract void gather(JsonStorage reciver) ;
	@Override
	public CompletableFuture<?> run(CachedOutput pOutput) {
		List<CompletableFuture<?>> work=new ArrayList<>();
		gather((k,v)->{
			Path p=output.getOutputFolder().resolve(type.getDirectory()+"/" + k.getNamespace() + "/"+k.getPath());
			work.add(saveJson(pOutput,v,p));
		});
		return CompletableFuture.allOf(work.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return name+" Json Generator";
	}
	private static CompletableFuture<?> saveJson(CachedOutput cache, JsonElement recipeJson, Path path) {
		return DataProvider.saveStable(cache, recipeJson, path);
	}
}
