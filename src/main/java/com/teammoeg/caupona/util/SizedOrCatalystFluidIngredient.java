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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public final class SizedOrCatalystFluidIngredient {
    /**
     * The "flat" codec for {@link SizedOrCatalystFluidIngredient}.
     *
     * <p>The amount is serialized inline with the rest of the ingredient, for example:
     *
     * <pre>{@code
     * {
     *     "fluid": "minecraft:water",
     *     "amount": 250
     * }
     * }</pre>
     *
     * <p>
     * <p>
     * Compound fluid ingredients are always serialized using the map codec, i.e.
     *
     * <pre>{@code
     * {
     *     "type": "neoforge:compound",
     *     "ingredients": [
     *         { "fluid": "minecraft:water" },
     *         { "fluid": "minecraft:milk" }
     *     ],
     *     "amount": 500
     * }
     * }</pre>
     *
     * <p>
     */
    public static final Codec<SizedOrCatalystFluidIngredient> FLAT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidIngredient.MAP_CODEC_NONEMPTY.forGetter(SizedOrCatalystFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", FluidType.BUCKET_VOLUME).forGetter(SizedOrCatalystFluidIngredient::amount))
            .apply(instance, SizedOrCatalystFluidIngredient::new));

    /**
     * The "nested" codec for {@link SizedOrCatalystFluidIngredient}.
     *
     * <p>With this codec, the amount is <i>always</i> serialized separately from the ingredient itself, for example:
     *
     * <pre>{@code
     * {
     *     "ingredient": {
     *         "fluid": "minecraft:lava"
     *     },
     *     "amount": 1000
     * }
     * }</pre>
     */
    public static final Codec<SizedOrCatalystFluidIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidIngredient.CODEC_NON_EMPTY.fieldOf("ingredient").forGetter(SizedOrCatalystFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", FluidType.BUCKET_VOLUME).forGetter(SizedOrCatalystFluidIngredient::amount))
            .apply(instance, SizedOrCatalystFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedOrCatalystFluidIngredient> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC,
            SizedOrCatalystFluidIngredient::ingredient,
            ByteBufCodecs.VAR_INT,
            SizedOrCatalystFluidIngredient::amount,
            SizedOrCatalystFluidIngredient::new);

    public static SizedOrCatalystFluidIngredient of(Fluid fluid, int amount) {
    	
        return new SizedOrCatalystFluidIngredient(FluidIngredient.of(fluid), amount);
    }

    /**
     * Helper method to create a simple sized ingredient that matches the given fluid stack
     */
    public static SizedOrCatalystFluidIngredient of(FluidStack stack) {
        return new SizedOrCatalystFluidIngredient(FluidIngredient.single(stack), stack.getAmount());
    }

    /**
     * Helper method to create a simple sized ingredient that matches fluids in a tag.
     */
    public static SizedOrCatalystFluidIngredient of(TagKey<Fluid> tag, int amount) {
        return new SizedOrCatalystFluidIngredient(FluidIngredient.tag(tag), amount);
    }

    private final FluidIngredient ingredient;
    private final int amount;

    @Nullable
    private FluidStack[] cachedStacks;

    public SizedOrCatalystFluidIngredient(FluidIngredient ingredient, int amount) {

        this.ingredient = ingredient;
        this.amount = amount;
    }

    public FluidIngredient ingredient() {
        return ingredient;
    }

    public int amount() {
        return amount;
    }

    /**
     * Performs a size-sensitive test on the given stack.
     *
     * @return {@code true} if the stack matches the ingredient and has at least the required amount.
     */
    public boolean test(FluidStack stack) {
        return ingredient.test(stack) && stack.getAmount() >= amount;
    }

    /**
     * Returns a list of the stacks from this {@link #ingredient}, with an updated {@link #amount}.
     *
     * @implNote the array is cached and should not be modified, just like {@link FluidIngredient#getStacks()}}.
     */
    public FluidStack[] getFluids() {
        if (cachedStacks == null) {
            cachedStacks = Stream.of(ingredient.getStacks())
                    .map(s -> s.copyWithAmount(amount>0?amount:1))
                    .toArray(FluidStack[]::new);
        }
        return cachedStacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizedOrCatalystFluidIngredient other)) return false;
        return amount == other.amount && ingredient.equals(other.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, amount);
    }

    @Override
    public String toString() {
        return amount + "x " + ingredient;
    }

}
