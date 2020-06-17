package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Widget;

public class KeepChildrenInside implements Constraint {

    public KeepChildrenInside() {

    }

    @Override
    public void apply(Widget container, Widget component) {
        if (component.getX() < container.getX()) {
            component.setX(container.getX());
        }

        if (component.getX() + component.getWidth() > container.getX() + container.getWidth()) {
            component.setX(container.getX() + container.getWidth() - component.getWidth());
        }

        if (component.getY() < container.getY()) {
            component.setY(container.getY());
        }

        if (component.getY() + component.getHeight() > container.getY() + container.getHeight()) {
            component.setY(container.getY() + container.getHeight() - component.getHeight());
        }
    }

    @Override
    public boolean shouldApply(Widget container, Widget component) {
        return true;
    }
}
