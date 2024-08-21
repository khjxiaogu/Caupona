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

package com.teammoeg.caupona.data.recipes;

import java.util.ArrayList;

import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

/**
 * For caching data and reduce calculation
 */
public class StewPendingContext extends IPendingContext {
	private StewInfo info;
	Fluid cur;
	private ResultCachingMap<StewBaseCondition, Integer> basetypes = new ResultCachingMap<>(
			e -> e.apply(info.base, cur));

	public Fluid getCur() {
		return cur;
	}

	public StewPendingContext(StewInfo info, Fluid current) {
		super();
		this.info = info;
		items = new ArrayList<>(info.stacks.size());
		for (FloatemStack fs : info.stacks) {
			FloatemTagStack fst = new FloatemTagStack(fs);
			items.add(fst);
			totalItems += fs.getCount();
		}

		cur = current;
	}

	public int compute(StewBaseCondition sbc) {
		return basetypes.compute(sbc);
	}

	public StewInfo getInfo() {
		return info;
	}
}
