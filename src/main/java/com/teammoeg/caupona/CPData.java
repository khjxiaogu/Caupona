package com.teammoeg.caupona;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.loot.AddPoolLootModifier;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class CPData {
	
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS=DeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CPMain.MODID);
	static {
		LOOT_MODIFIERS.register("add_table",()->AddPoolLootModifier.CODEC);
	}
}
