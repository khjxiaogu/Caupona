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

import java.util.List;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.decoration.mosaic.TBenchMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TBenchScreen extends AbstractContainerScreen<TBenchMenu> {
   private static final ResourceLocation BG_LOCATION = ResourceLocation.fromNamespaceAndPath(CPMain.MODID,"textures/gui/tessellation_workbench.png");
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int RECIPES_X = 52;
   private static final int RECIPES_Y = 14;

   public TBenchScreen(TBenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
      --this.titleLabelY;
   }

   public void render(GuiGraphics p_281735_, int p_282517_, int p_282840_, float p_282389_) {
      super.render(p_281735_, p_282517_, p_282840_, p_282389_);
      this.renderTooltip(p_281735_, p_282517_, p_282840_);
   }

   protected void renderBg(GuiGraphics p_283115_, float p_282453_, int p_282940_, int p_282328_) {
      int i = this.leftPos;
      int j = this.topPos;
      p_283115_.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      int l = this.leftPos + RECIPES_X;
      int i1 = this.topPos + RECIPES_Y;
      this.renderButtons(p_283115_, p_282940_, p_282328_, l, i1);
      this.renderRecipes(p_283115_, l, i1);
   }

   protected void renderTooltip(GuiGraphics p_282396_, int p_283157_, int p_282258_) {
      super.renderTooltip(p_282396_, p_283157_, p_282258_);
      if (!this.menu.getRecipes().isEmpty()) {
         int i = this.leftPos + RECIPES_X;
         int j = this.topPos + RECIPES_Y;
         List<ItemStack> list = this.menu.getRecipes();

         for(int l = 0;l < this.menu.getNumRecipes(); ++l) {
            int j1 = i + l % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int k1 = j + l / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            if (p_283157_ >= j1 && p_283157_ < j1 + RECIPES_IMAGE_SIZE_WIDTH && p_282258_ >= k1 && p_282258_ < k1 + RECIPES_IMAGE_SIZE_HEIGHT) {
               p_282396_.renderTooltip(this.font, list.get(l), p_283157_, p_282258_);
            }
         }
      }

   }

   private void renderButtons(GuiGraphics p_282733_, int p_282136_, int p_282147_, int p_281987_, int p_281276_) {
      for(int i = 0; i < this.menu.getNumRecipes(); ++i) {
         int k = p_281987_ + i % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
         int l = i / RECIPES_COLUMNS;
         int i1 = p_281276_ + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
         int j1 = this.imageHeight;
         if (i == this.menu.getSelectedRecipeIndex()) {
            j1 += RECIPES_IMAGE_SIZE_HEIGHT;
         } else if (p_282136_ >= k && p_282147_ >= i1 && p_282136_ < k + RECIPES_IMAGE_SIZE_WIDTH && p_282147_ < i1 + RECIPES_IMAGE_SIZE_HEIGHT) {
            j1 += 36;
         }

         p_282733_.blit(BG_LOCATION, k, i1 - 1, 0, j1, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
      }

   }

   private void renderRecipes(GuiGraphics p_281999_, int p_282658_, int p_282563_) {
      List<ItemStack> list = this.menu.getRecipes();

      for(int i = 0; i < this.menu.getNumRecipes(); ++i) {
         int k = p_282658_ + i % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
         int l = i / RECIPES_COLUMNS;
         int i1 = p_282563_ + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
         p_281999_.renderItem(list.get(i), k, i1);
      }

   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
	   if (!this.menu.getRecipes().isEmpty()) {
         int i = this.leftPos + RECIPES_X;
         int j = this.topPos + RECIPES_Y;
 

         for(int l = 0; l < RECIPES_ROWS*RECIPES_COLUMNS; ++l) {
            double d0 = pMouseX - (double)(i + l % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
            double d1 = pMouseY - (double)(j + l / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < RECIPES_IMAGE_SIZE_WIDTH && d1 < RECIPES_IMAGE_SIZE_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
               return true;
            }
         }
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }



}