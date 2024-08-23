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

package com.teammoeg.caupona.client.gui;

import java.util.ArrayList;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.dolium.DoliumContainer;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DoliumScreen extends AbstractContainerScreen<DoliumContainer> {
	public final CounterDoliumBlockEntity blockEntity;
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CPMain.MODID,
			"textures/gui/counter_with_dolium.png");

	public DoliumScreen(DoliumContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		blockEntity = screenContainer.getBlock();
	}

	private ArrayList<Component> tooltip = new ArrayList<>(2);

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		super.render(transform, mouseX, mouseY, partial);
		if (!blockEntity.tank.isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 80, 27, 16, 46)) {
				tooltip.add(blockEntity.tank.getFluid().getHoverName());
			}
			GuiUtils.handleGuiTank(transform.pose(), blockEntity.tank, leftPos + 80, topPos + 27, 16, 46);
		}
		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font,tooltip,Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX - 2, this.titleLabelY, 0xEEEEEE, false);
		matrixStack.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX - 2, this.inventoryLabelY - 3,4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		transform.blit(TEXTURE,leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (blockEntity.process > 0) {
			int w = (int) (12 * (blockEntity.process / (float) blockEntity.processMax));
			transform.blit(TEXTURE, leftPos + 117, topPos + 32, 176, 0, w, 25);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}
}
