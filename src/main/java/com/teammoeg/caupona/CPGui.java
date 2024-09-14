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

import com.teammoeg.caupona.blocks.decoration.mosaic.TBenchMenu;
import com.teammoeg.caupona.blocks.dolium.DoliumContainer;
import com.teammoeg.caupona.blocks.pan.PanContainer;
import com.teammoeg.caupona.blocks.pot.StewPotContainer;
import com.teammoeg.caupona.blocks.stove.KitchenStoveContainer;
import com.teammoeg.caupona.container.PortableBrazierContainer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPGui {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU,
			CPMain.MODID);
	public static final DeferredHolder<MenuType<?>,MenuType<StewPotContainer>> STEWPOT = CONTAINERS.register("stew_pot",
			() -> IMenuTypeExtension.create(StewPotContainer::new));
	public static final DeferredHolder<MenuType<?>,MenuType<KitchenStoveContainer>> STOVE = CONTAINERS.register("kitchen_stove",
			() -> IMenuTypeExtension.create(KitchenStoveContainer::new));
	public static final DeferredHolder<MenuType<?>,MenuType<DoliumContainer>> DOLIUM = CONTAINERS.register("dolium",
			() -> IMenuTypeExtension.create(DoliumContainer::new));
	public static final DeferredHolder<MenuType<?>,MenuType<PortableBrazierContainer>> BRAZIER = CONTAINERS
			.register("portable_brazier", () -> IMenuTypeExtension.create(PortableBrazierContainer::new));
	public static final DeferredHolder<MenuType<?>,MenuType<PanContainer>> PAN = CONTAINERS.register("pan",
			() -> IMenuTypeExtension.create(PanContainer::new));
	public static final DeferredHolder<MenuType<?>,MenuType<TBenchMenu>> T_BENCH = CONTAINERS.register("tessellation_workbench",
			() -> IMenuTypeExtension.create(TBenchMenu::new));

}