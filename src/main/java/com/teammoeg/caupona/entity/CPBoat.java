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

package com.teammoeg.caupona.entity;

import com.teammoeg.caupona.CPEntityTypes;
import com.teammoeg.caupona.CPMain;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CPBoat extends Boat {
	private static final EntityDataAccessor<String> WOOD_TYPE = SynchedEntityData.defineId(CPBoat.class,
			EntityDataSerializers.STRING);

	public CPBoat(EntityType<? extends Boat> p_38290_, Level p_38291_) {
		super(p_38290_, p_38291_);
	}

	public CPBoat(Level p_38293_, double p_38294_, double p_38295_, double p_38296_) {
		this(CPEntityTypes.BOAT.get(), p_38293_);
		this.setPos(p_38294_, p_38295_, p_38296_);
		this.xo = p_38294_;
		this.yo = p_38295_;
		this.zo = p_38296_;
	}
	@Override
	protected void defineSynchedData(Builder builder) {
		// TODO Auto-generated method stub
		super.defineSynchedData(builder);
		builder.define(WOOD_TYPE, "walnut");
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setWoodType(compound.getString("CPType"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("CPType", this.getWoodType());
	}

	public String getWoodType() {
		return this.entityData.get(WOOD_TYPE);
	}

	public void setWoodType(String wood) {
		this.entityData.set(WOOD_TYPE, wood);
	}

	@Override
	public Item getDropItem() {
		return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, getWoodType() + "_boat"));
	}



	/*@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}*/

}
