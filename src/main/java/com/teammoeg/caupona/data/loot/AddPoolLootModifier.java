package com.teammoeg.caupona.data.loot;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class AddPoolLootModifier extends LootModifier{
	public static final MapCodec<AddPoolLootModifier> CODEC = 
			RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
		.and(Codec.either(ResourceKey.codec(Registries.LOOT_TABLE), LootTable.DIRECT_CODEC).fieldOf("loot_table").forGetter(o->o.lootTable))
		.apply(inst, AddPoolLootModifier::new));
	Either<ResourceKey<LootTable>, LootTable> lootTable;
	protected AddPoolLootModifier(LootItemCondition[] conditionsIn,Either<ResourceKey<LootTable>, LootTable> table) {
		super(conditionsIn);
		this.lootTable=table;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
			LootContext context) {
		LootTable lt=lootTable.map(o->context.getResolver().get(Registries.LOOT_TABLE, (ResourceKey<LootTable>)o).map(Holder::value).orElse(LootTable.EMPTY),
			o->o);
		lt.getRandomItemsRaw(new LootContext.Builder(context).withQueriedLootTableId(lt.getLootTableId()).create(null),generatedLoot::add);
		//if(context.pushVisitedElement(LootContext.createVisitedEntry(lt))) {
		
		//}
		return generatedLoot;
	}
	public static Builder builder(ResourceLocation table) {
		return new Builder(table);
	}
	public static class Builder{
		List<LootItemCondition> cond=new ArrayList<>();
		ResourceLocation table;
		Builder(ResourceLocation table) {
			super();
			this.table = table;
		}
		public Builder when(LootItemCondition.Builder builder) {
			cond.add(builder.build());
			return this;
		}
		public AddPoolLootModifier build() {
			return new AddPoolLootModifier(cond.toArray(LootItemCondition[]::new),Either.left(ResourceKey.create(Registries.LOOT_TABLE, table)));
		}
	}
}
