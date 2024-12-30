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

package com.teammoeg.caupona.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class CPParticle extends TextureSheetParticle {
	protected float originalScale = 1.3F;
	private SpriteSet spriteSet;
	protected CPParticle(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z);
		
	}

	public CPParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	public void setSpriteSet(SpriteSet ss) {
		this.spriteSet=ss;
		this.setSpriteFromAge(this.spriteSet);
	}
	
	@Override
	public void render(VertexConsumer worldRendererIn, Camera entityIn, float pt) {
		float age = (this.age + pt) / lifetime * 32.0F;

		age = Mth.clamp(age, 0.0F, 1.0F);

		this.quadSize = originalScale * age;
		super.render(worldRendererIn, entityIn, pt);
	}

	@Override
	public void tick() {
		super.tick();

		if(this.spriteSet!=null)
			this.setSpriteFromAge(this.spriteSet);
	}


}
