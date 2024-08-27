package com.teammoeg.caupona.client.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class LayeredBakedModel implements BakedModel {
	protected Map<String,int[]> faces;
	protected List<BakedQuad> unculledFaces;
	protected final boolean hasAmbientOcclusion;
	protected final boolean isGui3d;
	protected final boolean usesBlockLight;
	protected final TextureAtlasSprite particleIcon;
	protected final ItemTransforms transforms;
	protected final ItemOverrides overrides;
	public final int cacheNo=0;
	protected final net.neoforged.neoforge.client.ChunkRenderTypeSet blockRenderTypes;
	protected final List<net.minecraft.client.renderer.RenderType> itemRenderTypes;
	protected final List<net.minecraft.client.renderer.RenderType> fabulousItemRenderTypes;
	private final Function<Map.Entry<String,int[]>,int[]> values;
	private final Function<int[],IntStream> istr;
	private final IntFunction<BakedQuad> tobaked;
	public LayeredBakedModel(List<BakedQuad> pUnculledFaces, boolean pHasAmbientOcclusion, boolean pUsesBlockLight,
			boolean pIsGui3d, TextureAtlasSprite pParticleIcon, ItemTransforms pTransforms, ItemOverrides pOverrides) {
		this(pUnculledFaces,new HashMap<>(),pHasAmbientOcclusion, pUsesBlockLight, pIsGui3d, pParticleIcon, pTransforms, pOverrides,
				net.neoforged.neoforge.client.RenderTypeGroup.EMPTY);
	}

	public LayeredBakedModel(List<BakedQuad> pUnculledFaces,Map<String,int[]> names, boolean pHasAmbientOcclusion, boolean pUsesBlockLight,
			boolean pIsGui3d, TextureAtlasSprite pParticleIcon, ItemTransforms pTransforms, ItemOverrides pOverrides,
			net.neoforged.neoforge.client.RenderTypeGroup renderTypes) {
		this.unculledFaces = pUnculledFaces;
		this.hasAmbientOcclusion = pHasAmbientOcclusion;
		this.isGui3d = pIsGui3d;
		this.usesBlockLight = pUsesBlockLight;
		this.particleIcon = pParticleIcon;
		this.transforms = pTransforms;
		this.overrides = pOverrides;
		this.faces=names;
		this.blockRenderTypes = !renderTypes.isEmpty()
				? net.neoforged.neoforge.client.ChunkRenderTypeSet.of(renderTypes.block())
				: null;
		this.itemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entity()) : null;
		this.fabulousItemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entityFabulous()) : null;
		values=Map.Entry::getValue;
		istr=Arrays::stream;
		tobaked=unculledFaces::get;
	}

	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		ImmutableSet<String> groups=data.get(DisplayGroupProperty.PROPERTY);
		if(groups!=null)
			return faces.entrySet().stream().filter(e->groups.contains(e.getKey())).map(values)
					.flatMapToInt(istr).distinct().mapToObj(tobaked).collect(Collectors.toUnmodifiableList());
		return this.unculledFaces;
	}

	public boolean useAmbientOcclusion() {
		return this.hasAmbientOcclusion;
	}

	public boolean isGui3d() {
		return this.isGui3d;
	}

	public boolean usesBlockLight() {
		return this.usesBlockLight;
	}

	public boolean isCustomRenderer() {
		return false;
	}

	public TextureAtlasSprite getParticleIcon() {
		return this.particleIcon;
	}

	public ItemTransforms getTransforms() {
		return this.transforms;
	}

	public ItemOverrides getOverrides() {
		return this.overrides;
	}

	@Override
	public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(
			@org.jetbrains.annotations.NotNull BlockState state, @org.jetbrains.annotations.NotNull RandomSource rand,
			@org.jetbrains.annotations.NotNull net.neoforged.neoforge.client.model.data.ModelData data) {
		if (blockRenderTypes != null)
			return blockRenderTypes;
		return BakedModel.super.getRenderTypes(state, rand, data);
	}

	@Override
	public List<net.minecraft.client.renderer.RenderType> getRenderTypes(net.minecraft.world.item.ItemStack itemStack,
			boolean fabulous) {
		if (!fabulous) {
			if (itemRenderTypes != null)
				return itemRenderTypes;
		} else {
			if (fabulousItemRenderTypes != null)
				return fabulousItemRenderTypes;
		}
		return BakedModel.super.getRenderTypes(itemStack, fabulous);
	}

	public static class Builder {
		private final List<BakedQuad> unculledFaces = Lists.newArrayList();
		private final Map<String,Set<Integer>> faces=new HashMap<>();
		private final ItemOverrides overrides;
		private final boolean hasAmbientOcclusion;
		private TextureAtlasSprite particleIcon;
		private final boolean usesBlockLight;
		private final boolean isGui3d;
		private final ItemTransforms transforms;

		public Builder(BlockModel pBlockModel, ItemOverrides pOverrides, boolean pIsGui3d) {
			this(pBlockModel.hasAmbientOcclusion(), pBlockModel.getGuiLight().lightLikeBlock(), pIsGui3d,
					pBlockModel.getTransforms(), pOverrides);
		}

		public Builder(boolean pHasAmbientOcclusion, boolean pUsesBlockLight, boolean pIsGui3d,
				ItemTransforms pTransforms, ItemOverrides pOverrides) {
			this.overrides = pOverrides;
			this.hasAmbientOcclusion = pHasAmbientOcclusion;
			this.usesBlockLight = pUsesBlockLight;
			this.isGui3d = pIsGui3d;
			this.transforms = pTransforms;
		}

		public LayeredBakedModel.Builder addUnculledFace(BakedQuad pQuad,Iterable<String> groups) {
			int idx=this.unculledFaces.size();
			this.unculledFaces.add(pQuad);
			for(String group:groups)
				faces.computeIfAbsent(group,e->new LinkedHashSet<>()).add(idx);
			return this;
		}

		public LayeredBakedModel.Builder particle(TextureAtlasSprite pParticleIcon) {
			this.particleIcon = pParticleIcon;
			return this;
		}

		public LayeredBakedModel.Builder item() {
			return this;
		}

		public BakedModel build(net.neoforged.neoforge.client.RenderTypeGroup renderTypes) {
			Map<String,int[]> rfaces=new HashMap<>();
			ToIntFunction<Integer> identity=e->e;
			for(Entry<String, Set<Integer>> k:faces.entrySet()) {
				rfaces.put(k.getKey(),k.getValue().stream().mapToInt(identity).toArray());
			}
			return new LayeredBakedModel(this.unculledFaces,rfaces, this.hasAmbientOcclusion, this.usesBlockLight,
					this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes);
		}
	}

	@Override
	public List<BakedQuad> getQuads(BlockState pState, Direction pDirection, RandomSource pRandom) {
		if(pDirection!=null)return List.of();
		return this.unculledFaces;
	}

}