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

package com.teammoeg.caupona.client.gui;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
	public int xTexStart;
	public int yTexStart;
	private final int textureWidth;
	private final int textureHeight;
	public int state;
	ResourceLocation texture;
	Supplier<Tooltip> tooltipProvider;
	private int laststate=-1;



	public ImageButton(Button.Builder builder, int xTexStart, int yTexStart, int textureWidth, int textureHeight,
			ResourceLocation texture, Supplier<Tooltip> tooltipProvider) {
		super(builder);
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.texture = texture;
		this.tooltipProvider = tooltipProvider;
	}
	public void setPosition(int xIn, int yIn) {
		super.setX(xIn);
		super.setY(yIn);
	}
	
	public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		int i = 0, j = state * this.height;
		if(state!=laststate)
			super.setTooltip(tooltipProvider.get());
		laststate=state;
		if (this.isHovered()) {
			i += this.width;
			
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,this.alpha);
		RenderSystem.enableDepthTest();
		matrixStack.blit(texture, this.getX(), this.getY(), this.xTexStart + i, this.yTexStart + j, this.width, this.height,
				this.textureWidth, this.textureHeight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

	}
}