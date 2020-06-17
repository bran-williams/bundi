package com.branwilliams.bundi.gui.api.actions;

public enum MouseWheelDirection {
	UP, DOWN;

	public int toInt() {
		return this == UP ? 1 : -1;
	}
}