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

package com.teammoeg.caupona.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

public abstract class CPBaseBlockEntity extends BlockEntity {

	public CPBaseBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}

	public abstract void handleMessage(short type, int data);

	public void sendMessage(short type, int data) {
		PacketHandler.sendToServer(new ClientDataMessage(this.worldPosition, type, data));
	}

	public void syncData() {
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		this.setChanged();
	}

	public abstract void readCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries);

	public abstract void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider registries);
	boolean fromNetwork=false;
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
		fromNetwork=true;
		super.onDataPacket(net, pkt, registries);
	}

	public abstract void tick();
	public Object getCapability(BlockCapability<?,Direction> type,Direction d) {
		return null;
	};
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		this.readCustomNBT(nbt, fromNetwork, registries);
		super.loadAdditional(nbt,registries);
		fromNetwork=false;

	}

	@Override
	protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		this.writeCustomNBT(compound, false, registries);
		super.saveAdditional(compound,registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		writeCustomNBT(nbt, true,registries);
		return nbt;
	}
}
