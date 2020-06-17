package com.branwilliams.bundi.gui.api.actions;

/**
 * This action occurs when a mouse button is pressed or released. <br/>
 * Created by Brandon Williams on 2/10/2017.
 */
public class ClickEvent {

    public enum MouseClickAction {
        MOUSE_PRESS, MOUSE_RELEASE;
    }

    /**
     * The mouse x position of this click.
     * */
    public final int x;

    /**
     * The mouse y position of this click.
     * */
    public final int y;

    /**
     * The id which represents the button pressed.
     * */
    public final int buttonId;

    public final MouseClickAction mouseClickAction;

    public ClickEvent(MouseClickAction mouseClickAction, int x, int y, int buttonId) {
        this.mouseClickAction = mouseClickAction;
        this.x = x;
        this.y = y;
        this.buttonId = buttonId;
    }

    public boolean isMousePressed() {
        return mouseClickAction == MouseClickAction.MOUSE_PRESS;
    }

    /**
     * Listener for the click action.
     * */
    public interface ClickActionListener extends ActionListener<ClickEvent> {

    }
}
