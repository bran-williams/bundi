package com.branwilliams.bundi.gui.api.containers;

import com.branwilliams.bundi.gui.api.*;
import com.branwilliams.bundi.gui.api.layouts.PaddedLayout;
import com.branwilliams.bundi.gui.api.layouts.ScrollableLayout;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickAction;
import com.branwilliams.bundi.gui.api.actions.Direction;
import com.branwilliams.bundi.gui.api.actions.WheelActionListener;

/**
 * Implementation of a scrollable container. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class ScrollableContainer extends Container {

    private Scrollbar verticalScrollbar, horizontalScrollbar;

    public ScrollableContainer(String tag) {
        super(tag);
        this.verticalScrollbar = new Scrollbar(toolbox, 8);
        this.horizontalScrollbar = new Scrollbar(toolbox, false,8);
        this.setLayout(new PaddedLayout());
        // Sets the SCROLLBAR activated
        this.addListener(Actions.MOUSE_PRESS, (ClickAction.ClickActionListener) action -> updateMouseClick(action.x, action.y));
        // Sets scrolling to false once the mouse button is released.
        this.addListener(Actions.MOUSE_RELEASE, (ClickAction.ClickActionListener) action -> {
            verticalScrollbar.setScrolling(false);
            horizontalScrollbar.setScrolling(false);
            return false;
        });

        // Scrolls when the mouse wheel is moved.
        this.addListener(Actions.MOUSEWHEEL, (WheelActionListener) direction -> updateMouseWheel(direction == Direction.UP ? -1 : 1));
    }

    @Override
    public void update() {
        verticalScrollbar.update();
        horizontalScrollbar.update();
        // Laid out before updating to ensure that the scrollbar's offset is always applied.
        if (verticalScrollbar.using() || horizontalScrollbar.using())
            layout();
        super.update();
    }

    @Override
    public boolean isPointInside(int x, int y) {
        // Include this to ensure that the components outside the viewable area are not interacted with.
        return super.isPointInside(x, y) && toolbox.isPointInside(x, y, new int[] {
                getX(),
                getY(),
                getWidth(),
                getHeight()
        });
    }

    /**
     * @return True if this container's SCROLLBAR was clicked and if this container's SCROLLBAR has been clicked upon.
     * */
    private boolean updateMouseClick(int mouseX, int mouseY) {
        if (isHovered()) {
            if (verticalScrollbar.has() && verticalScrollbar.isPointInside(mouseX, mouseY)) {
                verticalScrollbar.setScrolling(true);
                return true;
            } else if (horizontalScrollbar.has() && horizontalScrollbar.isPointInside(mouseX, mouseY)) {
                horizontalScrollbar.setScrolling(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the scroll bar based on the mouse wheel.
     * */
    private boolean updateMouseWheel(int direction) {
        if (isHovered() && isPointInside(toolbox.getMouseX(), toolbox.getMouseY())) {
            verticalScrollbar.wheel(direction);
            horizontalScrollbar.wheel(direction);
            return true;
        }
        return false;
    }

    /**
     * @return The area within the component that can be rendered within.
     * */
    public Widget getRenderArea() {
        return new BasicWidget(
                getX(),
                getY(),
                getWidth() - (verticalScrollbar.has() ? verticalScrollbar.getBarSize() : 0),
                getHeight() - (horizontalScrollbar.has() ? horizontalScrollbar.getBarSize() : 0)
        );
    }

    @Override
    public void layout() {
        verticalScrollbar.constrict();
        horizontalScrollbar.constrict();
        super.layout();
    }

    @Override
    protected void setToolbox(Toolbox toolbox) {
        super.setToolbox(toolbox);
        this.verticalScrollbar.setToolbox(toolbox);
        this.horizontalScrollbar.setToolbox(toolbox);
    }

    @Override
    public void setLayout(Layout layout) {
        // We want to wrap up every layout with a scrollable layout. This ensure that it only uses the 'renderabe area'
        // of this container when laying out components.
        if (!(layout instanceof ScrollableLayout)) {
            layout = new ScrollableLayout(layout);
        }
        super.setLayout(layout);
    }

    public Scrollbar getVerticalScrollbar() {
        return verticalScrollbar;
    }

    public Scrollbar getHorizontalScrollbar() {
        return horizontalScrollbar;
    }
}
