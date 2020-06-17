package com.branwilliams.bundi.gui.api.layouts;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;

import java.util.List;

/**
 * List styled layout used within scrollable containers. Will lay out each component to be within a list-order. <br/>
 * Created by Brandon Williams on 2/16/2017.
 */
public class ListLayout implements Layout<Widget, Widget> {

    // Padding used between the components and the container.
    private int padding;

    // Padding used between the components.
    private int componentPadding;

    // Used to determine whether or not this list layout will format the component in a vertical order or a horizontal order.
    private boolean vertical;

    // Forces the components to resize to the container's dimensions.
    private boolean forcedSize;

    public ListLayout(int padding) {
        this(padding, padding);
    }

    public ListLayout(int padding, int componentPadding) {
        this(padding, componentPadding, true);
    }

    public ListLayout(int padding, int componentPadding, boolean vertical) {
        this(padding, componentPadding, vertical, true);
    }

    public ListLayout(int padding, int componentPadding, boolean vertical, boolean forcedSize) {
        this.padding = padding;
        this.componentPadding = componentPadding;
        this.vertical = vertical;
        this.forcedSize = forcedSize;
    }

    @Override
    public int[] layout(Widget container, List<Widget> components) {
        // x and y positions are offset by the scroll offset when necessary.
        int x = container.getX() + padding;
        int y = container.getY() + padding;

        // Total area used in calculations within the scroll bar.
        int totalWidth = padding * 2, totalHeight = padding * 2;

        // Position each component within the container as necessary and update the total area.
        for (Widget component : components) {
            component.setX(x);
            component.setY(y);

            // Update the position with the components dimensions and apply padding.
            if (vertical) {
                y += component.getHeight() + componentPadding;
                totalHeight += component.getHeight() + componentPadding;

                if (forcedSize) {
                    component.setWidth(container.getWidth() - padding * 2);
                } else if (totalWidth < padding * 2 + component.getWidth()) {
                    totalWidth = padding * 2 + component.getWidth();
                }
            } else {
                x += component.getWidth() + componentPadding;
                totalWidth += component.getWidth() + componentPadding;

                if (forcedSize) {
                    component.setHeight(container.getHeight() - padding * 2);
                } else if (totalHeight < padding * 2 + component.getHeight()) {
                    totalHeight = padding * 2 + component.getHeight();
                }
            }
        }

        // If we have more than 0 components, there will be extra padding appended and it is necessary to remove this from the total area.
        if (components.size() > 0) {
            totalWidth -= componentPadding;
            totalHeight -= componentPadding;
        }
        if (vertical)
            return new int[] { container.getWidth(), totalHeight };
        else
            return new int[] { totalWidth, container.getHeight() };
    }
}
