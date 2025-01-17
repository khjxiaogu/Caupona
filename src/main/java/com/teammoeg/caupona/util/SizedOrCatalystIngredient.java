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

import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;


public final class SizedOrCatalystIngredient {
    public static final Codec<SizedOrCatalystIngredient> FLAT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.MAP_CODEC_NONEMPTY.forGetter(SizedOrCatalystIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.NON_NEGATIVE_INT, "count", 1).forGetter(SizedOrCatalystIngredient::count))
            .apply(instance, SizedOrCatalystIngredient::new));

    /**
     * The "nested" codec for {@link SizedIngredient}.
     *
     * <p>The count is serialized separately from the rest of the ingredient, for example:
     *
     * <pre>{@code
     * {
     *     "ingredient": {
     *         "item": "minecraft:apple"
     *     },
     *     "count": 3
     * }
     * }</pre>
     */
    public static final Codec<SizedOrCatalystIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SizedOrCatalystIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.NON_NEGATIVE_INT, "count", 1).forGetter(SizedOrCatalystIngredient::count))
            .apply(instance, SizedOrCatalystIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedOrCatalystIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SizedOrCatalystIngredient::ingredient,
            ByteBufCodecs.VAR_INT,
            SizedOrCatalystIngredient::count,
            SizedOrCatalystIngredient::new);

    /**
     * Helper method to create a simple sized ingredient that matches a single item.
     */
    public static SizedOrCatalystIngredient of(ItemLike item, int count) {
        return new SizedOrCatalystIngredient(Ingredient.of(item), count);
    }

    /**
     * Helper method to create a simple sized ingredient that matches items in a tag.
     */
    public static SizedOrCatalystIngredient of(TagKey<Item> tag, int count) {
        return new SizedOrCatalystIngredient(Ingredient.of(tag), count);
    }

    private final Ingredient ingredient;
    private final int count;
    @Nullable
    private ItemStack[] cachedStacks;

    public SizedOrCatalystIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public int count() {
        return count;
    }

    /**
     * Performs a size-sensitive test on the given stack.
     *
     * @return {@code true} if the stack matches the ingredient and has at least the required count.
     */
    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    /**
     * Returns a list of the stacks from this {@link #ingredient}, with an updated {@link #count}.
     *
     * @implNote the array is cached and should not be modified, just like {@link Ingredient#getItems()}.
     */
    public ItemStack[] getItems() {
        if (cachedStacks == null) {
            cachedStacks = Stream.of(ingredient.getItems())
                    .map(s -> s.copyWithCount(count>0?count:1))
                    .toArray(ItemStack[]::new);
        }
        return cachedStacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizedOrCatalystIngredient other)) return false;
        return count == other.count && ingredient.equals(other.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {
        return count + "x " + ingredient;
    }
}
