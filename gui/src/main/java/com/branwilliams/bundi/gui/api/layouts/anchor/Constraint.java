package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Widget;

/**
 * Constrains a component in some way.
 * */
public interface Constraint<Container extends Widget, Component extends Widget> {

    /**
     * Applies this constraint to the component.
     * */
    void apply(Container container, Component component);

    /**
     * @return True if this constraint should be applied to the component.
     * */
    boolean shouldApply(Container container, Component component);
}
