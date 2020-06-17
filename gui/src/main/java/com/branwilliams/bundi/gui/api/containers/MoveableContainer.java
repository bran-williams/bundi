package com.branwilliams.bundi.gui.api.containers;

import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

import java.util.function.BiFunction;

/**
 * Container which can be moved using the mouse.
 * */
public abstract class MoveableContainer extends Container {

    // Stored distance from the container's position to the mouse's position used to calculate movement.
    private int clickOffsetX, clickOffsetY;

    private boolean moving = false;

    private BiFunction<MoveableContainer, Toolbox, Boolean> moveFunction;

    public MoveableContainer() {
        super();
        //this.setAutoLayout(true);
        this.addListener(ClickEvent.class, (ClickEvent.ClickActionListener) action -> {
            // Sets moving to true once this container was clicked inside a moveable area.
            switch (action.mouseClickAction) {
                case MOUSE_PRESS:
                    if (isHovered() && isPointInsideMoveableArea(action.x, action.y) && action.buttonId == 0) {
                        clickOffsetX = action.x - getX();
                        clickOffsetY = action.y - getY();
                        moving = true;
                        return true;
                    }
                case MOUSE_RELEASE:
                    // Sets moving to false when the mouse is no longer being pressed.
                    clickOffsetX = 0;
                    clickOffsetY = 0;
                    moving = false;
            }
            return false;
        });

    }

    @Override
    public void update() {
        if (moving) {
            this.setPosition(toolbox.getMouseX() - clickOffsetX, toolbox.getMouseY() - clickOffsetY);
            if (moveFunction != null)
                moveFunction.apply(this, toolbox);
        }
        super.update();
    }

    /**
     * @return True when the x and y position is within the draggable area of this container.
     * */
    public abstract boolean isPointInsideMoveableArea(int x, int y);

    public void setMoveFunction(BiFunction<MoveableContainer, Toolbox, Boolean> moveFunction) {
        this.moveFunction = moveFunction;
    }
}
