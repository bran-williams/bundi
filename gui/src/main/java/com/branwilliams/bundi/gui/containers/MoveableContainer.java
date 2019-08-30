package com.branwilliams.bundi.gui.containers;

import com.branwilliams.bundi.gui.Container;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.actions.Actions;
import com.branwilliams.bundi.gui.actions.ClickAction;

import java.util.function.BiFunction;

/**
 * Container which can be moved using the mouse.
 * */
public abstract class MoveableContainer extends Container {

    // Stored distance from the container's position to the mouse's position used to calculate movement.
    private int clickOffsetX, clickOffsetY;

    private boolean moving = false;

    private BiFunction<MoveableContainer, Toolbox, Boolean> moveFunction;

    public MoveableContainer(String tag) {
        super(tag);
        //this.setAutoLayout(true);
        // Sets moving to true once this container was clicked inside a moveable area.
        this.addListener(Actions.MOUSE_PRESS, (ClickAction.ClickActionListener) action -> {
            if (isHovered() && isPointInsideMoveableArea(action.x, action.y) && action.buttonId == 0) {
                clickOffsetX = action.x - getX();
                clickOffsetY = action.y - getY();
                moving = true;
                return true;
            }
            return false;
        });
        // Sets moving to false when the mouse is no longer being pressed.
        this.addListener(Actions.MOUSE_RELEASE, (ClickAction.ClickActionListener) action -> {
            clickOffsetX = 0;
            clickOffsetY = 0;
            moving = false;
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
