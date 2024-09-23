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

package com.teammoeg.caupona.util;

import com.teammoeg.caupona.data.recipes.TimedRecipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeHandler<T extends Recipe<?>&TimedRecipe>{
	private int process;
	private int processMax;
	private ResourceLocation lastRecipe;
	private boolean recipeTested=false;
	private Runnable doRecipe;
	public ResourceLocation getLastRecipe() {
		return lastRecipe;
	}
	public RecipeHandler(Runnable doRecipe) {
		this.doRecipe=doRecipe;
	}
	public void onContainerChanged() {
		//System.out.println("revalidate needed");
		recipeTested=false;
	}
	public boolean shouldTestRecipe() {
		return !recipeTested;
	}
	public void setRecipe(RecipeHolder<T> recipe) {
		//System.out.println("revalidate return "+recipe);
		if (recipe!= null) {
			if(!recipe.id().equals(lastRecipe)) {
				process=processMax=recipe.value().getTime();
				lastRecipe=recipe.id();
			}
		}else {
			process=processMax=0;
			lastRecipe=null;
		}
		recipeTested=true;
	}
	public boolean tickProcess(int num) {
		if (process > 0) {
			process-=num;
			if(process<=0) {
				doRecipe.run();
				process=processMax=0;
				lastRecipe=null;
				recipeTested=false;
			}
			return true;
		}
		return false;
	}
	public void resetProgress() {
		process=processMax;
	}
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		if (!isClient) {
			if(nbt.contains("lastRecipe"))
				lastRecipe=ResourceLocation.parse(nbt.getString("lastRecipe"));
			else
				lastRecipe=null;
		}

	}
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.putInt("processMax",processMax);
		if (!isClient) {
			if(lastRecipe!=null)
				nbt.putString("lastRecipe", lastRecipe.toString());
		}

	}
	public int getProcess() {
		return process;
	}
	public int getProcessMax() {
		return processMax;
	}
	public int getFinishedProgress() {
		return processMax-process;
	}

}
