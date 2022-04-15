package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Layout;
import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;
import com.branwilliams.bundi.gui.api.layouts.ListLayout;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Simple button implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Button extends Container {

    private boolean active;

    private boolean pressed = false;

    private Color backgroundColor;

    private BiFunction<Button, ClickEvent, Boolean> pressFunction;

    public Button() {
        this(false);
    }

    public Button(boolean active) {
        this.active = active;
        this.setLayout(new ListLayout(0, 0));
        this.addListener(ClickEvent.class, (ClickEvent.ClickActionListener) event -> {
            switch (event.mouseClickAction) {
                case MOUSE_PRESS:
                    if (active && isHovered() && isPointInside(event.x, event.y)) {
                        pressed = true;
                        return true;
                    }
                    return false;

                case MOUSE_RELEASE:
                    // return true only if mouse release on top of button and button was pressed when initially clicked.
                    if (active && pressed && isPointInside(event.x, event.y)) {
                        if (pressFunction != null) {
                            pressFunction.apply(this, event);
                        }
                        pressed = false;
                        return true;
                    }
                    pressed = false;
                    return false;

                default:
                    return false;
            }
        });
    }

    @Override
    public void update() {
        super.update();
    }

    /**
     * Invokes the given function when this button is pressed.
     * */
    public void onPressed(BiFunction<Button, ClickEvent, Boolean> pressFunction) {
        this.pressFunction = pressFunction;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPressed() {
        return pressed;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "Button2{" +
                "components=" + components +
                ", active=" + active +
                ", pressed=" + pressed +
                ", backgroundColor=" + backgroundColor +
                ", pressFunction=" + (pressFunction == null ? "null" : "active") +
                '}';
    }

    private class Button2Layout implements Layout {
        @Override
        public int[] layout(Widget widget, List list) {
            return new int[0];
        }
    }
}
