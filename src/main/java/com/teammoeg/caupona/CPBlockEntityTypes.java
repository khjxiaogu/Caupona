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

package com.teammoeg.caupona;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.blocks.decoration.CPHangingSignBlockEntity;
import com.teammoeg.caupona.blocks.decoration.CPSignBlockEntity;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.foods.BowlBlockEntity;
import com.teammoeg.caupona.blocks.foods.DishBlockEntity;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlockEntity;
import com.teammoeg.caupona.blocks.hypocaust.CaliductBlockEntity;
import com.teammoeg.caupona.blocks.hypocaust.FireboxBlockEntity;
import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlockEntity;
import com.teammoeg.caupona.blocks.loaf.LoafDoughBlockEntity;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.blocks.stove.ChimneyPotBlockEntity;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.blocks.stove.KitchenStoveT1;
import com.teammoeg.caupona.blocks.stove.KitchenStoveT2;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister
			.create(Registries.BLOCK_ENTITY_TYPE, CPMain.MODID);

	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<StewPotBlockEntity>> STEW_POT = REGISTER.register("stew_pot",makeTypes2(StewPotBlockEntity::new,
					()->List.of(CPBlocks.STEW_POT,CPBlocks.STEW_POT_LEAD)));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<KitchenStoveBlockEntity>> STOVE_T1 = REGISTER.register("kitchen_stove_basic", makeTypes(KitchenStoveT1::new,
					()->CPBlocks.stoves.stream().map(e->e.get()).filter(e->e.getBlock()==CPBlockEntityTypes.STOVE_T1).collect(Collectors.toList())));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<KitchenStoveBlockEntity>> STOVE_T2 = REGISTER
			.register("kitchen_stove_fast", makeTypes(KitchenStoveT2::new,
					()->CPBlocks.stoves.stream().map(e->e.get()).filter(e->e.getBlock()==CPBlockEntityTypes.STOVE_T2).collect(Collectors.toList())));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BowlBlockEntity>> BOWL = REGISTER.register("bowl",makeTypes2(BowlBlockEntity::new,
					()->List.of(CPBlocks.BOWL,CPBlocks.LOAF_BOWL)));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CPSignBlockEntity>> SIGN = REGISTER.register("sign",makeTypes(CPSignBlockEntity::new,
					()->CPBlocks.signs));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CPHangingSignBlockEntity>> HANGING_SIGN = REGISTER.register("hanging_sign",makeTypes(CPHangingSignBlockEntity::new,
		()->CPBlocks.hanging_signs));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChimneyPotBlockEntity>> CHIMNEY_POT = REGISTER.register("chimney_pot",makeTypes(ChimneyPotBlockEntity::new,
					()->CPBlocks.chimney));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FumaroleVentBlockEntity>> FUMAROLE = REGISTER.register("fumarole_vent", makeTypes2(FumaroleVentBlockEntity::new,
					()->Arrays.asList(CPBlocks.FUMAROLE_VENT,CPBlocks.LITHARGE_FUMAROLE_VENT)));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PanBlockEntity>> PAN = REGISTER.register("pan", makeTypes2(PanBlockEntity::new,
					()->List.of(CPBlocks.STONE_PAN, CPBlocks.COPPER_PAN, CPBlocks.IRON_PAN, CPBlocks.LEAD_PAN )));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CounterDoliumBlockEntity>> DOLIUM = REGISTER.register("dolium",makeTypes(CounterDoliumBlockEntity::new,
					()->CPBlocks.dolium));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DishBlockEntity>> DISH = REGISTER.register("dish",makeTypes(DishBlockEntity::new,
					()->CPBlocks.dishes));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CaliductBlockEntity>> CALIDUCT = REGISTER.register("caliduct",makeTypes(CaliductBlockEntity::new,
					()->CPBlocks.caliduct));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FireboxBlockEntity>> HYPOCAUST_FIREBOX = REGISTER.register("hypocast_firebox",makeTypes(FireboxBlockEntity::new,
					()->CPBlocks.firebox));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WolfStatueBlockEntity>> WOLF_STATUE = REGISTER.register("wolf_statue",makeType(WolfStatueBlockEntity::new,
					()->CPBlocks.WOLF));
	public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LoafDoughBlockEntity>> LOAF_DOUGH = REGISTER.register("loaf_dough",makeType(LoafDoughBlockEntity::new,
		()->CPBlocks.LOAF_DOUGH));
	
	/*private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>,BlockEntityType<T>> register(String key,BlockEntitySupplier<T> factory,DeferredHolder<Block,? extends Block>...validBlocks){
		return REGISTER.register(key, () -> new BlockEntityType<T>(factory,(Arrays.stream(validBlocks).map(t->t.get()).collect(Collectors.toSet())), null));
	}*/
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntitySupplier<T> create,
			Supplier<DeferredHolder<Block,? extends Block>> valid) {
		return () -> new BlockEntityType<>(create, ImmutableSet.of(valid.get().get()), null);
	}
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypes2(BlockEntitySupplier<T> create,
			Supplier<List<DeferredHolder<Block,? extends Block>>> valid) {
		return () -> new BlockEntityType<>(create, valid.get().stream().map(DeferredHolder<Block,? extends Block>::get).collect(Collectors.toSet()), null);
	}
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypes(BlockEntitySupplier<T> create,
			Supplier<List<Block>> valid) {
		return () -> new BlockEntityType<>(create, ImmutableSet.copyOf(valid.get()), null);
	}
}