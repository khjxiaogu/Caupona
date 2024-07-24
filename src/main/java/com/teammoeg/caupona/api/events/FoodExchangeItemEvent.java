package com.teammoeg.caupona.api.events;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class FoodExchangeItemEvent extends Event {
	ItemStack origin;
	public FoodExchangeItemEvent(ItemStack origin) {
		super();
		this.origin = origin;
	}
	public static class Pre extends FoodExchangeItemEvent{

		public Pre(ItemStack origin) {
			super(origin);
		}
		
	}
	public static class Post extends FoodExchangeItemEvent{
		ItemStack target;
		public Post(ItemStack origin,ItemStack target) {
			super(origin);
			this.target=target;
		}
		public ItemStack getTarget() {
			return target;
		}
		
	}
	EventResult result;
	public ItemStack getOrigin() {
		return origin;
	}
	public EventResult getResult() {
		return result;
	}
	public void setResult(EventResult result) {
		this.result = result;
	}
}
