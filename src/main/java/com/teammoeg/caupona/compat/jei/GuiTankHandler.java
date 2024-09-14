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

package com.teammoeg.caupona.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.neoforge.fluids.FluidStack;

public class GuiTankHandler<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<T> {
	List<Pair<Rect2i,Function<T,FluidStack>>> lists=new ArrayList<>();
	IIngredientManager manager;


	public GuiTankHandler(IIngredientManager manager) {
		super();
		this.manager = manager;
	}
	public GuiTankHandler<T> addTank(int x,int y,int w,int h,Function<T,FluidStack> f) {
		lists.add(Pair.of(new Rect2i(x,y,w,h), f));
		return this;
	}

	@Override
	public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
		
		double x=mouseX-containerScreen.getGuiLeft();
		double y=mouseY-containerScreen.getGuiTop();
		for(Pair<Rect2i, Function<T, FluidStack>> i:lists) {
			if (x >= i.getFirst().getX() &&
				y >= i.getFirst().getY() &&
					x < i.getFirst().getX() + i.getFirst().getWidth() &&
					y < i.getFirst().getY() + i.getFirst().getHeight()) {
				return Optional.ofNullable(i.getSecond().apply(containerScreen)).filter(t->!t.isEmpty()).flatMap(t->manager.createTypedIngredient(NeoForgeTypes.FLUID_STACK,t))
					.map(o->new ClickableIngredient<>(o,i.getFirst()));
			}
		}
		return Optional.empty();
	}

}
