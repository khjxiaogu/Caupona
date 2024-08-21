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

package com.teammoeg.caupona.client;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPEntityTypes;
import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.gui.DoliumScreen;
import com.teammoeg.caupona.client.gui.KitchenStoveScreen;
import com.teammoeg.caupona.client.gui.PanScreen;
import com.teammoeg.caupona.client.gui.PortableBrazierScreen;
import com.teammoeg.caupona.client.gui.StewPotScreen;
import com.teammoeg.caupona.client.gui.TBenchScreen;
import com.teammoeg.caupona.client.particle.SootParticle;
import com.teammoeg.caupona.client.particle.SteamParticle;
import com.teammoeg.caupona.client.renderer.BowlRenderer;
import com.teammoeg.caupona.client.renderer.CPBoatRenderer;
import com.teammoeg.caupona.client.renderer.CounterDoliumRenderer;
import com.teammoeg.caupona.client.renderer.PanRenderer;
import com.teammoeg.caupona.client.renderer.StewPotRenderer;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CPMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CPClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		LayerDefinition layer = BoatModel.createBodyModel();
		for (String wood : CPBlocks.woods)
			ClientHooks.registerLayerDefinition(
					new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "boat/" + wood), "main"), () -> layer);

		/*ItemBlockRenderTypes.setRenderLayer(CPBlocks.stew_pot, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove1, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove2, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove3, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove4, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove5, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.bowl, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.GRAVY_BOAT, RenderType.translucent());*/
		BlockEntityRenderers.register(CPBlockEntityTypes.STEW_POT.get(), StewPotRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.BOWL.get(), BowlRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.SIGN.get(), SignRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.DOLIUM.get(), CounterDoliumRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.PAN.get(), PanRenderer::new);
		Sheets.addWoodType(CPBlocks.WALNUT);
		EntityRenderers.register(CPEntityTypes.BOAT.get(), c->new CPBoatRenderer(c,false));

	}
	
	@SubscribeEvent
	public static void registerParticleFactories(RegisterMenuScreensEvent event) {
		event.register(CPGui.STEWPOT.get(), StewPotScreen::new);
		event.register(CPGui.STOVE.get(), KitchenStoveScreen::new);
		event.register(CPGui.DOLIUM.get(), DoliumScreen::new);
		event.register(CPGui.BRAZIER.get(), PortableBrazierScreen::new);
		event.register(CPGui.PAN.get(), PanScreen::new);
		event.register(CPGui.T_BENCH.get(), TBenchScreen::new);
	}
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CPParticles.STEAM.get(), SteamParticle.Factory::new);
		event.registerSpriteSet(CPParticles.SOOT.get(), SootParticle.Factory::new);
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Block ev) {
		ev.register((p_92626_, p_92627_, p_92628_, p_92629_) -> {
			return p_92627_ != null && p_92628_ != null ? BiomeColors.getAverageFoliageColor(p_92627_, p_92628_)
					: FoliageColor.getDefaultColor();
		}, CPBlocks.leaves.stream().map(t->t.value()).toArray(Block[]::new));
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Item ev) {
		ev.register((i, t) -> 0x5bd449,CPBlocks.leaves.stream().map(t->t.value()).toArray(Block[]::new));
	}
}