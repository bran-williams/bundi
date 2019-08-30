package com.branwilliams.bundi.gui.layouts.anchor;

import com.branwilliams.bundi.gui.Widget;

/**
 * Constrains a component's position and size based on some criteria.
 * */
public interface Constraint {

    /**
     * @return The new width and height of the component.
     * */
    int[] setPosition(Widget container, Widget component);
}
