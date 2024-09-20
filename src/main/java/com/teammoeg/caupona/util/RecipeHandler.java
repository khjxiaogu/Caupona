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
