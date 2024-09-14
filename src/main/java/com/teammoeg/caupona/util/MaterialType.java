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

package com.teammoeg.caupona.util;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;

public class MaterialType {
	String name;
	Supplier<BlockState> base;
	public MaterialType(String name) {
		super();
		this.name = name;
	}
	private boolean hasDeco;
	public MaterialType makeDecoration(){
		this.hasDeco=true;
		return this;
	}
	private int counterGrade;
	public MaterialType makeCounter(int grade){
		this.counterGrade=grade;
		return this;
	}
	private boolean hasPill;
	public MaterialType makePillar(){
		this.hasPill=true;
		return this;
	}
	private boolean hasHypo;
	public MaterialType makeHypocaust(){
		this.hasHypo=true;
		return this;
	}
	private boolean hasRoad;
	public MaterialType makeRoad(){
		this.hasRoad=true;
		return this;
	}
	public MaterialType setBase(Supplier<BlockState> base){
		this.base=base;
		return this;
	}
	public String getName() {
		return name;
	}
	public boolean isPillarMaterial() {
		return hasPill;
	}
	public boolean isDecorationMaterial() {
		return hasDeco;
	}
	public int getCounterGrade() {
		return counterGrade;
	}
	public boolean isCounterMaterial() {
		return counterGrade!=0;
	}
	public boolean isHypocaustMaterial() {
		return hasHypo;
	}
	public boolean isRoadMaterial() {
		return hasRoad;
	}
	public Supplier<BlockState> getBase() {
		return base;
	}
}
