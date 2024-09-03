package com.teammoeg.caupona.util;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;

public class LazyTickWorker {
	public int tMax;
	public int tCur=0;
	private boolean isStaticMax;
	public Supplier<Boolean> work;
	public LazyTickWorker(int tMax, Supplier<Boolean> work) {
		super();
		this.tMax = tMax;
		this.work = work;
		isStaticMax=true;
	}
	public LazyTickWorker(Supplier<Boolean> work) {
		super();
		this.work = work;
		isStaticMax=false;
	}
	public boolean tick() {
		if(tMax!=0) {
			tCur++;
			if(tCur>=tMax) {
				tCur=0;
				return work.get();
			}
		}
		return false;
	}
	public boolean isRunning() {
		return tMax!=0;
	}
	public void rewind() {
		tCur=0;
	}
	public void enqueue() {
		tCur=tMax;
	}
	public void start(int time) {
		tCur=0;
		tMax=time;
	}
	public void stop() {
		tMax=0;
	}
	public void read(CompoundTag cnbt) {
		if(!isStaticMax)
			tMax=cnbt.getInt("max");
		tCur=cnbt.getInt("cur");
	}
	public void read(CompoundTag cnbt,String key) {
		if(!isStaticMax)
			tMax=cnbt.getInt(key+"max");
		tCur=cnbt.getInt(key);
	}
	public CompoundTag write(CompoundTag cnbt) {
		if(!isStaticMax)
			cnbt.putInt("max", tMax);;
		cnbt.putInt("cur",tCur);
		return cnbt;
	}
	public CompoundTag write(CompoundTag cnbt,String key) {
		if(!isStaticMax)
			cnbt.putInt(key+"max", tMax);
		cnbt.putInt(key,tCur);
		return cnbt;
	}
}
