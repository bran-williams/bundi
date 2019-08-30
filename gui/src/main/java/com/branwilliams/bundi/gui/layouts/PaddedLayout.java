package com.branwilliams.bundi.gui.layouts;


import com.branwilliams.bundi.gui.Widget;
import com.branwilliams.bundi.gui.Layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of the layout interface. <br/>
 * Simply saves the components original positions and force them to follow the container's position. <br/>
 * Will enforce a vertical and/or horizontal padding. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class PaddedLayout implements Layout<Widget> {

    /**
     * Store the positions of each component.
     * */
    protected final Map<Widget, int[]> positions = new HashMap<>();

    protected int verticalPadding, horizontalPadding;

    public PaddedLayout() {
        this(0, 0);
    }

    public PaddedLayout(int padding) {
        this(padding, padding);
    }

    public PaddedLayout(int verticalPadding, int horizontalPadding) {
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
    }

    @Override
    public int[] layout(Widget container, List<Widget> components) {
        int width = 0, height = 0;
        for (Widget component : components) {
            int[] positions = this.positions.computeIfAbsent(component, (c) -> new int[] { c.getX(), c.getY() });
            component.setX(container.getX() + horizontalPadding + positions[0]);
            component.setY(container.getY() + verticalPadding + positions[1]);

            // Calculate the area these components occupy.
            if (positions[0] + component.getWidth() + horizontalPadding * 2 > width)
                width = positions[0] + component.getWidth() + horizontalPadding * 2;
            if (positions[1] + component.getHeight() + verticalPadding * 2 > height)
                height = positions[1] + component.getHeight() + verticalPadding * 2;
        }
        // System.out.println(width + ", " + height + ": " + components.size());
        return new int[] { width, height };
    }

    public void setPadding(int padding) {
        this.setHorizontalPadding(padding);
        this.setVerticalPadding(padding);
    }

    public int getVerticalPadding() {
        return verticalPadding;
    }

    public void setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
    }

    public int getHorizontalPadding() {
        return horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }
}
