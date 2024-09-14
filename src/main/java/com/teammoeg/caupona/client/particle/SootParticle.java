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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SootParticle extends CPParticle {

	public SootParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY,
			double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.gravity = -0.1F;
		this.rCol = this.gCol = this.bCol = (float) (Math.random() * 0.2) + 0.8f;
		this.originalScale = 0.25F;
		this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
		this.alpha = 0.75f;
		this.friction=0.96F;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			SootParticle steamParticle = new SootParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			steamParticle.setSpriteSet(this.spriteSet);
			return steamParticle;
		}
	}
}
