package com.branwilliams.bundi.gui.layouts.anchor;

import com.branwilliams.bundi.gui.Widget;

public class Anchor implements Constraint {

    private final Widget component, relativeTo;

    private AnchorType anchorType;

    public Anchor(AnchorType anchorType, Widget component, Widget relativeTo) {
        this.anchorType = anchorType;
        this.component = component;
        this.relativeTo = relativeTo;
    }

    @Override
    public int[] setPosition(Widget container, Widget component) {

        return new int[0];
    }

    public enum AnchorType {

    }
}
