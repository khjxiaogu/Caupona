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

package com.teammoeg.caupona.item;

import java.util.List;
import java.util.function.Predicate;

import com.teammoeg.caupona.entity.CPBoat;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CPBoatItem extends CPItem {
	private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
	private final String type;

	public CPBoatItem(String pType, Item.Properties pProperties) {
		super(pProperties,TabType.MAIN_AND_TRANSPORTATION);
		this.type = pType;
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when
	 * this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.ANY);
		if (hitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		}
		Vec3 vec3 = pPlayer.getViewVector(1.0F);
		List<Entity> list = pLevel.getEntities(pPlayer,
				pPlayer.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
		if (!list.isEmpty()) {
			Vec3 vec31 = pPlayer.getEyePosition();

			for (Entity entity : list) {
				AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
				if (aabb.contains(vec31)) {
					return InteractionResultHolder.pass(itemstack);
				}
			}
		}

		if (hitresult.getType() == HitResult.Type.BLOCK) {
			CPBoat boat = new CPBoat(pLevel, hitresult.getLocation().x, hitresult.getLocation().y,
					hitresult.getLocation().z);
			boat.setWoodType(type);
			boat.setYRot(pPlayer.getYRot());
			if (!pLevel.noCollision(boat, boat.getBoundingBox())) {
				return InteractionResultHolder.fail(itemstack);
			}
			if (!pLevel.isClientSide) {
				pLevel.addFreshEntity(boat);
				Vec3 hitpos=hitresult.getLocation();
				pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, new BlockPos((int)hitpos.x(),(int)hitpos.y(),(int)hitpos.z()));
				if (!pPlayer.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
			}

			pPlayer.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
		}
		return InteractionResultHolder.pass(itemstack);
	}
}