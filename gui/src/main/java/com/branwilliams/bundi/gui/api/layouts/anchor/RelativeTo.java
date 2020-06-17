package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Widget;

public class RelativeTo implements Constraint {

    public enum HorizontalConstraint {
        LEFT_ALIGN, CENTER, RIGHT_ALIGN, NONE;
    }

    public enum VerticalConstraint {
        BOTTOM, CENTER, TOP, NONE;
    }

    private HorizontalConstraint horizontalConstraint;

    private VerticalConstraint verticalConstraint;

    private int padding;

    public RelativeTo(HorizontalConstraint horizontalConstraint, VerticalConstraint verticalConstraint, int padding) {
        if (horizontalConstraint == HorizontalConstraint.NONE && verticalConstraint == VerticalConstraint.NONE) {
            throw new IllegalArgumentException("Both constraints cannot be none!");
        }
        this.horizontalConstraint = horizontalConstraint;
        this.verticalConstraint = verticalConstraint;
        this.padding = padding;
    }

    @Override
    public void apply(Widget container, Widget component) {
        switch (horizontalConstraint) {
            case LEFT_ALIGN:
                component.setX(container.getX() + padding);
                break;
            case CENTER:
                component.setX(container.getX() + (container.getWidth() / 2) - (component.getWidth() / 2));
                break;
            case RIGHT_ALIGN:
                component.setX(container.getX() + container.getWidth() - component.getWidth() - padding);
                break;
            default:
                break;
        }

        switch (verticalConstraint) {
            case BOTTOM:
                component.setY(container.getY() + container.getHeight() - component.getHeight() - padding);
                break;
            case CENTER:
                component.setY(container.getY() + (container.getHeight() / 2) - (component.getHeight() / 2));
                break;
            case TOP:
                component.setY(container.getY() + padding);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean shouldApply(Widget container, Widget component) {
        return true;
    }
}
