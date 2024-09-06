package com.teammoeg.caupona.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

public class CPDataMapProvider extends DataMapProvider {

	public CPDataMapProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void gather() {

	}

}
