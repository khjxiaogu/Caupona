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

package com.teammoeg.caupona.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.teammoeg.caupona.client.util.DisplayGroupProperty;

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
import net.neoforged.neoforge.common.util.ConcatenatedListView;

public class LayeredBakedModel implements BakedModel {
	protected Map<String,List<BakedQuad>> faces;
	protected Map<ImmutableSet<String>,List<BakedQuad>> modelCache=new ConcurrentHashMap<>();
	private Function<ImmutableSet<String>,List<BakedQuad>> cacheFunction;
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
	//private final Function<Map.Entry<String,int[]>,IntStream> values;
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
		//values=t->Arrays.stream(t.getValue());
		tobaked=unculledFaces::get;
		this.faces=names.entrySet().stream().collect(Collectors.toMap(t->t.getKey(),t->IntStream.of(t.getValue()).mapToObj(tobaked).collect(Collectors.toUnmodifiableList())));
		this.blockRenderTypes = !renderTypes.isEmpty()
				? net.neoforged.neoforge.client.ChunkRenderTypeSet.of(renderTypes.block())
				: null;
		this.itemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entity()) : null;
		this.fabulousItemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entityFabulous()) : null;
		cacheFunction=t->{
			List<List<BakedQuad>> listview=new ArrayList<>(t.size());
			for(String s:t) {
				List<BakedQuad> face=faces.get(s);
				if(face!=null)
					listview.add(face);
			}
			return ConcatenatedListView.of(listview);
		};

	}

	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		ImmutableSet<String> groups=data.get(DisplayGroupProperty.PROPERTY);
		if(groups!=null) {
			
			return modelCache.computeIfAbsent(groups,cacheFunction);
		}
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