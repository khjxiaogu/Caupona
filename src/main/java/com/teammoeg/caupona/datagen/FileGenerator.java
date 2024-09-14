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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.hash.Hashing;
import com.teammoeg.caupona.CPMain;

import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class FileGenerator implements DataProvider {
	protected final PackOutput output;
	protected ExistingFileHelper helper;
	private String name;
	PackType type;
	protected class FileStorage implements BiConsumer<Path,byte[]>,Consumer<FileOutput> {
		CachedOutput out;
		public FileStorage(CachedOutput out) {
			super();
			this.out = out;
		}
		public void accept(ResourceLocation rl,byte[] s) {
			Path p=output.getOutputFolder().resolve(type.getDirectory()+"/" + rl.getNamespace() + "/"+rl.getPath());
			this.accept(p, s);
		}
		@Override
		public void accept(Path t, byte[] u) {
			saveFile(out, u, t);
		}
		public void accept(Path t,String s) {
			this.accept(t, s.getBytes(StandardCharsets.UTF_8));
		}
		public void accept(ResourceLocation rl,String s) {
			this.accept(rl, s.getBytes(StandardCharsets.UTF_8));
		}
		@Override
		public void accept(FileOutput t) {
			this.accept(t.out,t.stream.toByteArray());
		}
		
	}
	public static class FileOutput{
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		PrintStream ps;
		Path out;
		private FileOutput(Path out) {
			super();
			this.out = out;
		}
		public OutputStream getOutput() {
			return stream;
		}
		public PrintStream getPrint() {
			if(ps!=null)return ps;
			return ps=new PrintStream(stream,true,StandardCharsets.UTF_8);
		}
	}
	public FileGenerator(PackType pt,PackOutput output, ExistingFileHelper helper, String name) {
		super();
		this.type=pt;
		this.output = output;
		this.helper = helper;
		this.name = name;
	}
	protected abstract void gather(FileStorage reciver) ;
	@Override
	public CompletableFuture<?> run(CachedOutput pOutput) {
		List<CompletableFuture<?>> work=new ArrayList<>();
		gather(new FileStorage(pOutput));
		return CompletableFuture.allOf(work.toArray(CompletableFuture[]::new));
	}
	protected Path getDatagenOutput() {
		return output.getOutputFolder();
	}
	protected Path getJavaOutput() {
		return output.getOutputFolder().getParent().getParent().resolve("main/java");
	}
	protected Path getClassPath(String classPath) {
		return getJavaOutput().resolve(String.join("/",classPath.split("\\."))+".java");
	}
	protected String getGeneratedPackage() {
		return "com.teammoeg."+CPMain.MODID+".generated";
	}
	protected Path getGeneratedClassPath(String className) {
		return getClassPath(getGeneratedPackage()+"."+className);
	}
	public FileOutput createOutput(Path t) {
		return new FileOutput(t);
	}
	public JavaFileOutput createJavaOutput(String classPath) {
		return new JavaFileOutput(this,classPath);
	}
	public JavaFileOutput createGeneratedJavaOutput(String classPath) {
		return new JavaFileOutput(this,getGeneratedPackage()+"."+classPath);
	}
	@Override
	public String getName() {
		return name+" File Generator";
	}
	@SuppressWarnings("deprecation")
	static CompletableFuture<?> saveFile(CachedOutput cache,byte[] data, Path path) {
	      return CompletableFuture.runAsync(() -> {
	          try {

	             cache.writeIfNeeded(path,data,Hashing.sha1().hashBytes(data));
	          } catch (IOException ioexception) {
	             LOGGER.error("Failed to save file to {}", path, ioexception);
	          }

	       }, Util.backgroundExecutor());
	}
}
