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

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.entity.CPBoat;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FluidItemWrapper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = CPMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CPCommonBootStrap {
	public static final List<Pair<Supplier<? extends ItemLike>, Float>> compositables = new ArrayList<>();
	public static final List<Pair<Supplier<? extends Block>,Pair<Integer,Integer>>> flamables=new ArrayList<>();
	@SubscribeEvent
	public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		CreativeTabItemHelper helper = new CreativeTabItemHelper(event.getTabKey(), event.getTab());
		CPItems.ITEMS.getEntries().forEach(e -> {
			if (e.get() instanceof ICreativeModeTabItem item) {
				item.fillItemCategory(helper);
			}
		});
		helper.register(event);

	}
	@SubscribeEvent
	public static void onCapabilityInject(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.FluidHandler.ITEM,(stack,o)->new FluidHandlerItemStack(CPCapability.SIMPLE_FLUID,stack,1250), CPItems.situla.get());
		//event.registerItem(Capabilities.FluidHandler.ITEM,(stack,o)->new FluidHandlerItemStack(CPCapability.SIMPLE_FLUID,stack,1250), CPItems.situla.get());
		event.registerItem(CPCapability.FOOD_INFO,(stack,o)->stack.get(CPCapability.STEW_INFO.get()), CPItems.stews.toArray(Item[]::new));
		event.registerItem(CPCapability.FOOD_INFO,(stack,o)->stack.get(CPCapability.SAUTEED_INFO.get()), CPItems.dish.toArray(Item[]::new));
		CPBlockEntityTypes.REGISTER.getEntries().stream().map(t->t.get()).forEach(be->{
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType<?>)be,
				(block,ctx)->(IItemHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.ItemHandler.BLOCK, ctx));
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType<?>)be,
				(block,ctx)->(IFluidHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.FluidHandler.BLOCK, ctx));
			});
		event.registerItem(Capabilities.FluidHandler.ITEM,(stack,o)->new FluidItemWrapper(stack), CPItems.stews.toArray(Item[]::new));
	}

	public static <R extends ItemLike,T extends R> DeferredHolder<R,T> asCompositable(DeferredHolder<R,T> obj, float val) {
		compositables.add(Pair.of(obj, val));
		return obj;
	}
	public static <R extends Block,T extends R> DeferredHolder<R,T> asFlamable(DeferredHolder<R,T> obj,int v1,int v2) {
		flamables.add(Pair.of(obj, Pair.of(v1, v2)));
		return obj;
	}

	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		registerDispensers();
		compositables.forEach(p -> ComposterBlock.COMPOSTABLES.put(p.getFirst().get(), (float) p.getSecond()));
		FireBlock fire=(FireBlock) Blocks.FIRE;
		flamables.forEach(p->fire.setFlammable(p.getFirst().get(), p.getSecond().getFirst(), p.getSecond().getSecond()));
	}

	public static void registerDispensers() {
		DispenserBlock.registerBehavior(Items.BOWL, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				FluidState fs = bp.level().getBlockState(front).getFluidState();
				BlockEntity blockEntity = bp.level().getBlockEntity(front);
				if (blockEntity != null) {
					IFluidHandler ip=bp.level().getCapability(Capabilities.FluidHandler.BLOCK,front, d.getOpposite());
					if (ip!=null) {
						ItemStack ret = CauponaApi.fillBowl(ip).orElse(null);
						if (ret != null) {
							if (is.getCount() == 1)
								return ret;
							is.shrink(1);
							if (!bp.blockEntity().insertItem(ret).isEmpty())
								this.defaultBehaviour.dispense(bp, ret);
						}
					} else if (blockEntity instanceof PanBlockEntity pan) {
						ItemStack out = pan.inv.getStackInSlot(10);
						if (!out.isEmpty()) {
							pan.inv.setStackInSlot(10, ItemStack.EMPTY);
							if (!bp.blockEntity().insertItem(out).isEmpty())
								this.defaultBehaviour.dispense(bp, out);
						}
					}

					return is;
				} else if (!fs.isEmpty()) {
					ItemStack ret = CauponaApi.fillBowl(new FluidStack(fs.getType(), 250)).orElse(null);
					if (ret != null) {
						if (is.getCount() == 1)
							return ret;
						is.shrink(1);
						if (!bp.blockEntity().insertItem(ret).isEmpty())
							this.defaultBehaviour.dispense(bp, ret);
					}
					return is;
				}
				return this.defaultBehaviour.dispense(bp, is);
			}

		});
		DispenserBlock.registerBehavior(CPItems.redstone_ladle.get(), new DefaultDispenseItemBehavior() {
			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				BlockPos back = bp.pos().relative(d.getOpposite());
				Block src = bp.level().getBlockState(front).getBlock();
				Optional<IFluidHandler> blockSource = FluidUtil.getFluidHandler(bp.level(), front,
						d.getOpposite());
				BlockEntity besrc=bp.level().getBlockEntity(front);
				BlockEntity blockTarget = bp.level().getBlockEntity(back);
				if (blockTarget != null) {
					@Nullable IFluidHandler iptar =bp.level().getCapability(Capabilities.FluidHandler.BLOCK,back, d);
					if (iptar!=null) {
						if (blockSource.isPresent()) {
							FluidUtil.tryFluidTransfer(iptar, blockSource.orElse(null), 250, true);

						} else if (src instanceof BucketPickup bpu) {
							FluidUtil.tryFluidTransfer(iptar,
									new BucketPickupHandlerWrapper(null,bpu, bp.level(), front), FluidType.BUCKET_VOLUME,
									true);
						}else if(besrc instanceof IFoodContainer cont) {
							for(int i=0;i<cont.getSlots();i++) {
								ItemStack its=cont.getInternal(i);
								IFluidHandlerItem ifhi=FluidHandler.ITEM.getCapability(its, null);
								if(ifhi!=null) {
									FluidStack fs=ifhi.getFluidInTank(0);
									if(!fs.isEmpty()) {
										if(iptar.fill(fs, FluidAction.SIMULATE)==fs.getAmount()) {
											iptar.fill(fs, FluidAction.EXECUTE);
											cont.setInternal(i,its.getCraftingRemainingItem());
											break;
										}
									}
								}

							}
						}
					}else if(blockTarget instanceof IFoodContainer contt) {

						@Nullable IFluidHandler ipsrc = bp.level().getCapability(Capabilities.FluidHandler.BLOCK,front, d.getOpposite());
						if(besrc instanceof IFoodContainer cont) {
							outer:for(int i=0;i<cont.getSlots();i++) {
								ItemStack its=cont.getInternal(i);

								if(!its.isEmpty()&&Utils.isExtractAllowed(its)) {
									for(int j=0;j<contt.getSlots();j++) {
										ItemStack its2=contt.getInternal(j);
										if(Utils.isExchangeAllowed(its, its2)&&cont.accepts(i, its2)&&contt.accepts(j, its)) {
											cont.setInternal(i, its2);
											contt.setInternal(j, its);
											break outer;
										}
									}
								}
							}
						}else if(ipsrc!=null){
							IFluidHandler tank=ipsrc;

							FluidStack fs=tank.drain(250, FluidAction.SIMULATE);
							if(!fs.isEmpty()) {
								for(int j=0;j<contt.getSlots();j++) {
									ItemStack its2=contt.getInternal(j);
									if(its2.getCount()==1) {
										ContanerContainFoodEvent ev=Utils.contain(its2, fs,true);
										if(ev.isAllowed()) {
											if(contt.accepts(j, ev.out)) {
												fs=tank.drain(ev.drainAmount, FluidAction.EXECUTE);
												if(fs.getAmount()==ev.drainAmount) {
													ev=Utils.contain(its2, fs,false);
													contt.setInternal(j,ev.out);
												}
											}
											break;
										}
									}
								}
							}
						}

					}

					return is;
				}
				return is;
			}

		});
		DispenserBlock.registerBehavior(CPItems.walnut_boat.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource pSource, ItemStack pStack) {
				Direction direction = pSource.state().getValue(DispenserBlock.FACING);
				Level level = pSource.level();
				double d0 = pSource.pos().getX() + direction.getStepX() * 1.125F;
				double d1 = pSource.pos().getY() + direction.getStepY() * 1.125F;
				double d2 = pSource.pos().getZ() + direction.getStepZ() * 1.125F;
				BlockPos blockpos = pSource.pos().relative(direction);
				double d3;
				if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
					d3 = 1.0D;
				} else {
					if (!level.getBlockState(blockpos).isAir()
							|| !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
						return this.defaultDispenseItemBehavior.dispense(pSource, pStack);
					}

					d3 = 0.0D;
				}

				Boat boat = new CPBoat(level, d0, d1 + d3, d2);
				boat.setYRot(direction.toYRot());
				level.addFreshEntity(boat);
				pStack.shrink(1);
				return pStack;
			}

			@Override
			protected void playSound(BlockSource pSource) {
				pSource.level().levelEvent(1000, pSource.pos(), 0);
			}
		});
		DispenserBlock.registerBehavior(CPItems.gravy_boat.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				BlockState bs=bp.level().getBlockState(front);
				if (bs.is(CPBlocks.GRAVY_BOAT.get())) {
					int idmg = is.getDamageValue();
					is.setDamageValue(bs.getValue(GravyBoatBlock.LEVEL));
					bp.level().setBlockAndUpdate(front, bs.setValue(GravyBoatBlock.LEVEL, idmg));
					return is;
				}
				return this.defaultBehaviour.dispense(bp, is);
			}

		});
		DispenseItemBehavior idispenseitembehavior1 = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			/**
			 * Dispense the specified stack, play the dispense sound and spawn particles.
			 */
			@Override
			@SuppressWarnings("resource")
			public ItemStack execute(BlockSource source, ItemStack stack) {

				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				@Nullable IFluidHandler ip = source.level().getCapability(Capabilities.FluidHandler.BLOCK,front, d.getOpposite());
				if (ip!=null) {
					FluidActionResult fa = FluidUtil.tryEmptyContainerAndStow(stack, ip, null, 1250,
							null, true);
					if (fa.isSuccess()) {
						if (fa.getResult() != null)
							return fa.getResult();
						stack.shrink(1);

					}
					return stack;
				}


				return this.defaultBehaviour.dispense(source, stack);
			}
		};
		DispenserBlock.registerBehavior(Items.MILK_BUCKET, idispenseitembehavior1);
		DefaultDispenseItemBehavior ddib = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				FluidStack fs = FluidHandler.ITEM.getCapability(stack, null).getFluidInTank(0);
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (!fs.isEmpty()) {
					if (blockEntity instanceof StewPotBlockEntity pot) {
						if (pot.tryAddFluid(fs)) {
							ItemStack ret = stack.getCraftingRemainingItem();
							if (stack.getCount() == 1)
								return ret;
							stack.shrink(1);
							if (!source.blockEntity().insertItem(ret).isEmpty())
								this.defaultBehaviour.dispense(source, ret);
						}
					} else if (blockEntity != null) {
						@Nullable IFluidHandler ip = source.level().getCapability(Capabilities.FluidHandler.BLOCK,front, d.getOpposite());
						if (ip!=null) {
							IFluidHandler handler = ip;
							if (handler.fill(fs, FluidAction.SIMULATE) == fs.getAmount()) {
								handler.fill(fs, FluidAction.EXECUTE);
								ItemStack ret = stack.getCraftingRemainingItem();
								if (stack.getCount() == 1)
									return ret;
								stack.shrink(1);
								if (!source.blockEntity().insertItem(ret).isEmpty())
									this.defaultBehaviour.dispense(source, ret);
							}
						}
					}
					return stack;
				}
				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		for (Item i : CPItems.stews) {
			DispenserBlock.registerBehavior(i, ddib);
		}
		DefaultDispenseItemBehavior spice = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemStack ospice = pot.getInv().getStackInSlot(11);
					pot.getInv().setStackInSlot(11, stack);
					return ospice;
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemStack ospice = ((PanBlockEntity) blockEntity).getInv().getStackInSlot(11);
					pan.getInv().setStackInSlot(11, stack);
					return ospice;
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemStack ospice = dolium.getInv().getStackInSlot(3);
					dolium.getInv().setStackInSlot(3, stack);
					return ospice;
				}

				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		DefaultDispenseItemBehavior pot = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemStack ospice = pot.getInv().getStackInSlot(11);
					pot.getInv().setStackInSlot(11, ItemStack.EMPTY);
					if (!source.blockEntity().insertItem(ospice).isEmpty())
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemStack ospice = pan.getInv().getStackInSlot(11);
					pan.getInv().setStackInSlot(11, ItemStack.EMPTY);
					if (!source.blockEntity().insertItem(ospice).isEmpty())
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemStack ospice = dolium.getInv().getStackInSlot(3);
					dolium.getInv().setStackInSlot(3, ItemStack.EMPTY);
					if (!source.blockEntity().insertItem(ospice).isEmpty())
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				}

				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		DispenserBlock.registerBehavior(Items.FLOWER_POT, pot);
		for (DeferredHolder<Item,Item> i : CPItems.spicesItems) {
			DispenserBlock.registerBehavior(i.get(), spice);
		}
	}

}
