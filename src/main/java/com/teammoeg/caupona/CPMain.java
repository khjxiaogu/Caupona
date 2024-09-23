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

package com.teammoeg.caupona;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teammoeg.caupona.client.CPParticles;
import com.teammoeg.caupona.compat.top.TOPRegister;
import com.teammoeg.caupona.network.PacketHandler;
import com.teammoeg.caupona.util.FuelType;
import com.teammoeg.caupona.util.Utils;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CPMain.MODID)
public class CPMain {

	public static final String MODID = "caupona";
	public static final String MODNAME = "Caupona";
	public static final Logger logger = LogManager.getLogger(MODNAME);
	public static final String BOOK_NBT_TAG=CPMain.MODID+":book_given";
	public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CPMain.MODID);
	public static final DeferredHolder<CreativeModeTab,CreativeModeTab> main=TABS.register("aaa_caupona_cpn_main",()->CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS).icon(()->new ItemStack(CPBlocks.STEW_POT.get())).title(Utils.translate("itemGroup.caupona")).build());
	public static final DeferredHolder<CreativeModeTab,CreativeModeTab> decoration=TABS.register("aaa_caupona_cpn_decorations",()->CreativeModeTab.builder().withTabsBefore(main.getKey()).icon(()->new ItemStack(CPBlocks.PUMICE_BLOOM.get())).title(Utils.translate("itemGroup.caupona_decorations")).build());
	public static final DeferredHolder<CreativeModeTab,CreativeModeTab> foods=TABS.register("aaa_caupona_cpn_food", ()->CreativeModeTab.builder().withTabsBefore(main.getKey(),decoration.getKey()).icon(()->new ItemStack(CPItems.gravy_boat.get())).title(Utils.translate("itemGroup.caupona_foods")).build());

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public CPMain(IEventBus mod) {
		NeoForgeMod.enableMilkFluid();
		mod.addListener(PacketHandler::registerPackets);
		mod.addListener(this::enqueueIMC);
		CPBlockEntityTypes.REGISTER.register(mod);//
		CPGui.CONTAINERS.register(mod);//
		CPParticles.REGISTER.register(mod);
		CPFluids.FLUIDS.register(mod);//
		CPFluids.FLUID_TYPES.register(mod);//
		CPBlocks.BLOCKS.register(mod);//
		CPItems.ITEMS.register(mod);//
		CPMain.TABS.register(mod);//
		CPRecipes.RECIPE_SERIALIZERS.register(mod);//
		CPEntityTypes.ENTITY_TYPES.register(mod);//
		CPRecipes.RECIPE_TYPES.register(mod);//
		CPWorldGen.STRUCTURE_TYPES.register(mod);//
		CPWorldGen.FOILAGE_TYPES.register(mod);//
		CPWorldGen.TRUNK_TYPES.register(mod);//
		CPMobEffects.EFFECTS.register(mod);//
		CPData.LOOT_MODIFIERS.register(mod);//
		CPCapability.REGISTRY.register(mod);//
		CPConfig.register();
		FuelType.OTHER.cold_ash();
	}

	@SuppressWarnings("unused")
	public void enqueueIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo("theoneprobe", "getTheOneProbe", ()->(Function<ITheOneProbe, ?>)TOPRegister::register);
	   // InterModComms.sendTo("treechop", "getTreeChopAPI", () -> (Consumer)TreechopCompat::new);
	}
}
