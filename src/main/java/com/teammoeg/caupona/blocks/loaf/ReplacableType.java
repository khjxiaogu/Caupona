package com.teammoeg.caupona.blocks.loaf;

public enum ReplacableType {
	EMPTY,
	ALLOW,
	FULL,
	DENY;
	public boolean shouldSkip() {
		return this==DENY;
	}
	public boolean isAllowed() {
		return this==EMPTY||this==ALLOW;
	}
}
