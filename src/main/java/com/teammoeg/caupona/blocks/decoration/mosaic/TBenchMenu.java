package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPTags;
import com.teammoeg.caupona.container.CPBaseContainer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TBenchMenu extends CPBaseContainer<BlockEntity> {
	private final ContainerLevelAccess access;
	/** The index of the selected recipe in the GUI. */
	private final DataSlot selectedRecipeIndex = DataSlot.standalone();
	private final Level level;
	private List<ItemStack> recipes = Lists.newArrayList();
	/**
	 * The {@linkplain net.minecraft.world.item.ItemStack} set in the input slot by
	 * the player.
	 */
	private ItemStack input0 = ItemStack.EMPTY;
	private ItemStack input1 = ItemStack.EMPTY;
	private ItemStack input2 = ItemStack.EMPTY;
	/**
	 * Stores the game time of the last time the player took items from the the
	 * crafting result slot. This is used to prevent the sound from being played
	 * multiple times on the same tick.
	 */
	long lastSoundTime;
	final Slot inputSlot0;
	final Slot inputSlot1;
	final Slot inputSlot2;
	/** The inventory slot that stores the output of the crafting recipe. */
	final Slot resultSlot;
	Runnable slotUpdateListener = () -> {
	};
	public final Container container = new SimpleContainer(3) {
		/**
		 * For block entities, ensures the chunk containing the block entity is saved to
		 * disk later - the game won't think it hasn't changed and skip it.
		 */
		public void setChanged() {
			super.setChanged();
			TBenchMenu.this.slotsChanged(this);
			TBenchMenu.this.slotUpdateListener.run();
		}
	};
	/** The inventory that stores the output of the crafting recipe. */
	final ResultContainer resultContainer = new ResultContainer();

	public TBenchMenu(int pContainerId, Inventory pPlayerInventory,FriendlyByteBuf buf) {
		this(pContainerId, pPlayerInventory,ContainerLevelAccess.create(pPlayerInventory.player.level(), buf.readBlockPos()));
		
	}

	public TBenchMenu(int pContainerId, Inventory pPlayerInventory,ContainerLevelAccess pAccess) {
		super(CPGui.T_BENCH.get(),null, pContainerId,4);
		this.access = pAccess;
		this.level = pPlayerInventory.player.level();
		this.inputSlot0 = this.addSlot(new Slot(this.container, 0, 20, 34) {
			@Override
			public boolean mayPlace(ItemStack input) {
				return input.is(CPTags.Items.MOSAIC_BASE)||input.is(CPBlocks.MOSAIC.get().asItem());
				
			}
		}) ;
		this.inputSlot1 = this.addSlot(new Slot(this.container, 1, 20, 16){
			@Override
			public boolean mayPlace(ItemStack input) {
				return MosaicMaterial.fromItem(input)!=null;
				
			}
		});
		this.inputSlot2 = this.addSlot(new Slot(this.container, 2, 20, 52){
			@Override
			public boolean mayPlace(ItemStack input) {
				return MosaicMaterial.fromItem(input)!=null;
				
			}
		});
		this.resultSlot = this.addSlot(new Slot(this.resultContainer, 3, 143, 33) {
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots
			 * as well as furnace fuel.
			 */
			public boolean mayPlace(ItemStack p_40362_) {
				return false;
			}

			public void onTake(Player player, ItemStack is) {
				is.onCraftedBy(player.level(), player, is.getCount());
				TBenchMenu.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());
				ItemStack input0=TBenchMenu.this.inputSlot0.getItem();
				if (input0.is(CPTags.Items.MOSAIC_BASE)) {
					TBenchMenu.this.inputSlot1.remove(1);
					TBenchMenu.this.inputSlot2.remove(1);
				}
				ItemStack itemstack = TBenchMenu.this.inputSlot0.remove(1);
				if (!itemstack.isEmpty()) {
					TBenchMenu.this.setupResultSlot();
				}

				pAccess.execute((p_40364_, p_40365_) -> {
					long l = p_40364_.getGameTime();
					if (TBenchMenu.this.lastSoundTime != l) {
						p_40364_.playSound((Player) null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT,
								SoundSource.BLOCKS, 1.0F, 1.0F);
						TBenchMenu.this.lastSoundTime = l;
					}

				});
				super.onTake(player, is);
			}

			private List<ItemStack> getRelevantItems() {
				return List.of(TBenchMenu.this.inputSlot0.getItem(),TBenchMenu.this.inputSlot1.getItem(),TBenchMenu.this.inputSlot2.getItem());
			}
		});

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
		}

		this.addDataSlot(this.selectedRecipeIndex);
	}

	/**
	 * Returns the index of the selected recipe.
	 */
	public int getSelectedRecipeIndex() {
		return this.selectedRecipeIndex.get();
	}

	public List<ItemStack> getRecipes() {
		return this.recipes;
	}

	public int getNumRecipes() {
		return this.recipes.size();
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean stillValid(Player pPlayer) {
		return (stillValid(this.access, pPlayer,CPBlocks.T_BENCH.get()));
		//return true;
		//
	}

	/**
	 * Handles the given Button-click on the server, currently only used by
	 * enchanting. Name is for legacy.
	 */
	public boolean clickMenuButton(Player pPlayer, int pId) {
		if (this.isValidRecipeIndex(pId)) {
			this.selectedRecipeIndex.set(pId);
			this.setupResultSlot();
		}

		return true;
	}

	private boolean isValidRecipeIndex(int pRecipeIndex) {
		return pRecipeIndex >= 0 && pRecipeIndex < this.recipes.size();
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void slotsChanged(Container pInventory) {
		ItemStack itemstack0 = this.inputSlot0.getItem();
		ItemStack itemstack1 = this.inputSlot1.getItem();
		ItemStack itemstack2 = this.inputSlot2.getItem();
		if (!itemstack0.is(this.input0.getItem())
				||!itemstack1.is(this.input1.getItem())
				||!itemstack2.is(this.input2.getItem())) {
			this.input0 = itemstack0.copy();
			this.input1 = itemstack1.copy();
			this.input2 = itemstack2.copy();
			this.setupRecipeList(pInventory, itemstack0);
		}

	}

	private void setupRecipeList(Container pContainer,ItemStack input0) {
		this.recipes.clear();
		this.selectedRecipeIndex.set(-1);
		this.resultSlot.set(ItemStack.EMPTY);
		if (input0.is(CPTags.Items.MOSAIC_BASE)) {
			MosaicMaterial m1 = MosaicMaterial.fromItem(inputSlot1.getItem());
			MosaicMaterial m2 = MosaicMaterial.fromItem(inputSlot2.getItem());
			if (m1 != null && m2 != null) {
				for (MosaicPattern pat : MosaicPattern.values()) {
					ItemStack is = new ItemStack(CPBlocks.MOSAIC.get());
					MosaicItem.setMosaic(is, m1, m2, pat);
					recipes.add(is);
				}
			}
		} else if (input0.is(CPBlocks.MOSAIC.get().asItem())) {
			@Nullable MosaicData tag = inputSlot0.getItem().get(CPCapability.MOSAIC_DATA);
			if (tag==null)
				return;
			MosaicMaterial m1 = tag.material1;
			MosaicMaterial m2 = tag.material2;
			if (m1 != null && m2 != null) {
				for (MosaicPattern pat : MosaicPattern.values()) {
					ItemStack is = new ItemStack(CPBlocks.MOSAIC.get());
					MosaicItem.setMosaic(is, m1, m2, pat);
					recipes.add(is);
				}
			}
		}

	}

	void setupResultSlot() {
		if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
			ItemStack itemstack = this.recipes.get(this.selectedRecipeIndex.get()).copy();
			if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
				this.resultContainer.setRecipeUsed(null);
				this.resultSlot.set(itemstack);
			} else {
				this.resultSlot.set(ItemStack.EMPTY);
			}
		} else {
			this.resultSlot.set(ItemStack.EMPTY);
		}

		this.broadcastChanges();
	}

	public MenuType<?> getType() {
		return CPGui.T_BENCH.get();
	}

	public void registerUpdateListener(Runnable pListener) {
		this.slotUpdateListener = pListener;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */
	public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
		return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
	}


	/**
	 * Called when the container is closed.
	 */
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.resultContainer.removeItemNoUpdate(1);
		this.access.execute((p_40313_, p_40314_) -> {
			this.clearContainer(pPlayer, this.container);
		});
	}

	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		// TODO Auto-generated method stub
		return this.moveItemStackTo(slotStack, 0, 3, false);
	}
}