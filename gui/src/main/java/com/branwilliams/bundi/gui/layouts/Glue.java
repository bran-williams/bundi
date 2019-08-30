package com.branwilliams.bundi.gui.layouts;

import com.branwilliams.bundi.gui.Widget;

/**
 * Formats the x and y positions of dimensions.
 * */
public interface Glue {
	
	boolean modifiesX();
	
	boolean modifiesY();
	
	boolean isRight();
	
	boolean isLeft();
	
	boolean isTop();
	
	boolean isBottom();
	
	boolean isCenterX();
	
	boolean isCenterY();

	/**
	 * Formats the X position of a widget using this glues formatting.
	 * Can format based on an outside widget.
	 * @param outside The outside widget.
	 * @param widget The widget being formatted by this glue.
	 * */
	void formatX(Widget outside, Widget widget);

	/**
	 * Formats the Y position of a widget using this glues formatting.
	 * Can format based on an outside widget.
	 * @param outside The outside widget.
	 * @param widget The widget being formatted by this glue.
	 * */
	void formatY(Widget outside, Widget widget);
	
}
