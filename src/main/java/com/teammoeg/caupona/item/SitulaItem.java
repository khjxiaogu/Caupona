/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class SitulaItem extends Item  implements ICreativeModeTabItem{
    public SitulaItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }


	@Override
	public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		@Nullable IFluidHandlerItem e=stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(e!=null){
			FluidStack f=e.getFluidInTank(0);
			if(!f.isEmpty()) {
				tooltip.add(f.getHoverName());
				StewInfo info = Utils.getOrCreateInfo(f);
				FloatemStack fs = info.stacks.stream()
						.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
						.orElse(null);
				if (fs != null)
					tooltip.add(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getHoverName()));
				ResourceLocation rl = info.spiceName;
				if (rl != null)
					tooltip.add(Utils.translate("tooltip.caupona.spice",
							Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
				if (info.base != null&&!info.stacks.isEmpty())
					tooltip.add(Utils.translate("tooltip.caupona.base", 
							info.base.getFluidType().getDescription()));
				PotionContents.addPotionTooltip(info.effects, tooltip::add, 1,20);

				tooltip.add(Utils.string(f.getAmount()+"/1250 mB"));
			}
		}
		
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand pUsedHand) {
		BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
		ItemStack cur=playerIn.getItemInHand(pUsedHand);
		if (ray.getType() == Type.BLOCK) {
			BlockPos blockpos = ray.getBlockPos();
			FluidState state = worldIn.getFluidState(blockpos);
			BlockState blk=worldIn.getBlockState(blockpos);
			
			if(blk.getBlock() instanceof BucketPickup bucket) {
				IFluidHandlerItem handler=cur.getCapability(Capabilities.FluidHandler.ITEM);
				if(handler!=null) {
					FluidStack fluid=handler.getFluidInTank(0);
					if(!fluid.isEmpty()&&fluid.getAmount()<handler.getTankCapacity(0)&&fluid.getFluid().isSame(state.getType())) {
						int amt=handler.fill(new FluidStack(state.getType(),FluidType.BUCKET_VOLUME),FluidAction.EXECUTE);
						if(amt>0) {
							bucket.pickupBlock(playerIn,worldIn, blockpos, blk);
							return InteractionResultHolder.sidedSuccess(cur,worldIn.isClientSide);
						}
					}
				}
			}
			FluidActionResult res=FluidUtil.tryPickUpFluid(cur, playerIn, worldIn, blockpos,ray.getDirection());
			if(res.isSuccess()) {
				
				return InteractionResultHolder.sidedSuccess(res.getResult(),worldIn.isClientSide);
			}
		}else if(ray.getType() == Type.MISS) {
			if(playerIn.isShiftKeyDown()) {
				IFluidHandlerItem handler=cur.getCapability(Capabilities.FluidHandler.ITEM);
				if(handler!=null) {
					FluidStack fluid=handler.getFluidInTank(0);
					if(!fluid.isEmpty()) {
						if(handler.drain(1250, FluidAction.EXECUTE).getAmount()>0) {
							return InteractionResultHolder.sidedSuccess(cur,worldIn.isClientSide);
						}
					}
				}
			}
		}
		return InteractionResultHolder.pass(cur);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(TabType.MAIN))
			helper.accept(this);
	}

}
