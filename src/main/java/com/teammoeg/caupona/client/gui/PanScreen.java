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
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pan.PanContainer;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PanScreen extends AbstractContainerScreen<PanContainer> {
	static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CPMain.MODID, "textures/gui/frying_pan.png");

	PanBlockEntity blockEntity;

	public PanScreen(PanContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 7;
		this.inventoryLabelY = this.imageHeight - 92;
		this.inventoryLabelX = 4;
		blockEntity = container.getBlock();
	}

	public static MutableComponent start = Utils.translate("gui." + CPMain.MODID + ".stewpot.canstart");
	public static MutableComponent started = Utils.translate("gui." + CPMain.MODID + ".stewpot.started");
	public static MutableComponent nostart = Utils.translate("gui." + CPMain.MODID + ".stewpot.cantstart");
	public static MutableComponent nors = Utils.translate("gui." + CPMain.MODID + ".stewpot.noredstone");
	public static MutableComponent rs = Utils.translate("gui." + CPMain.MODID + ".stewpot.redstone");
	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;

	@Override
	public void init() {
		super.init();
		this.clearWidgets();
		this.addRenderableWidget(btn1 = new ImageButton(Button.builder(start, btn -> {
			if (btn1.state == 0)
				blockEntity.sendMessage((short) 0, 0);
		}).pos(leftPos + 7, topPos + 48).size(20, 12), 176, 83, 256, 256, TEXTURE,
				() -> (btn1.state == 0 ? Tooltip.create(start) : Tooltip.create(started))));
		this.addRenderableWidget(btn2 = new ImageButton(Button.builder(rs, btn -> {
			blockEntity.sendMessage((short) 1, btn2.state);
		}).pos(leftPos + 7, topPos + 61).size(20, 20), 176, 107, 256, 256, TEXTURE,
				() -> (btn2.state == 2 ? Tooltip.create(rs) : Tooltip.create(nors))));

	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		btn1.state = blockEntity.processMax > 0 ? 1 : 0;
		btn2.state = blockEntity.rsstate ? 1 : 2;
		super.render(transform, mouseX, mouseY, partial);
		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font,tooltip,Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		int w = this.font.width(name.getString());
		matrixStack.drawString(this.font, name, this.imageWidth - w - this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (blockEntity.processMax > 0 && blockEntity.process > 0) {
			int h = (int) (29 * (blockEntity.process / (float) blockEntity.processMax));
			transform.blit(TEXTURE, leftPos + 39, topPos + 16 + h, 176, 54 + h, 16, 29 - h);
		}
		if (blockEntity.processMax > 0) {
			transform.blit(TEXTURE, leftPos + 61, topPos + 12, 176, 0, 54, 54);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
