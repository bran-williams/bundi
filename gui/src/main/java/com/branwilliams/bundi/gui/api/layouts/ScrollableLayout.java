package com.branwilliams.bundi.gui.api.layouts;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;
import com.branwilliams.bundi.gui.api.containers.ScrollableContainer;

import java.util.List;

/**
 * Layout made to update scrollable containers with a nested layout.
 *
 * */
public class ScrollableLayout implements Layout<ScrollableContainer> {

    private Layout layout;

    public ScrollableLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public int[] layout(ScrollableContainer container, List<Widget> components) {
        // Ensure the scroll bars are positioned accurately.
        container.getHorizontalScrollbar().updatePosition(container);
        container.getVerticalScrollbar().updatePosition(container);

        // Use the layout provided to calculate the dimensions which the components now take up.
        int[] dimensions = layout == null ? new int[] { container.getWidth(), container.getHeight() } : layout.layout(container.getRenderArea(), components);

        // Update the component positions based on the scrollbar offsets.
        for (Widget component : components) {
            component.setPosition(component.getX() - container.getHorizontalScrollbar().getScrollOffset(), component.getY() - container.getVerticalScrollbar().getScrollOffset());
        }

        // Update the total area for the scroll bars with those dimensions.
        container.getHorizontalScrollbar().setTotalAreaLength(dimensions[0]);
        container.getVerticalScrollbar().setTotalAreaLength(dimensions[1]);

        return new int[] { dimensions[0], dimensions[1] };
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
