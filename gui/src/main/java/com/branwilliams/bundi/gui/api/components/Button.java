package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

import java.awt.*;
import java.util.function.BiFunction;

/**
 * Simple button implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Button extends Component {

    private String text;

    private boolean active;

    private boolean pressed = false;

    private Color backgroundColor;

    private Color textColor;

    private BiFunction<Button, ClickEvent, Boolean> pressFunction;

    public Button(String text) {
        this(text, false);
    }

    public Button(String text, boolean active) {
        super();
        this.text = text;
        this.active = active;

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

    }

    /**
     * Invokes the given function when this button is pressed.
     * */
    public void onPressed(BiFunction<Button, ClickEvent, Boolean> pressFunction) {
        this.pressFunction = pressFunction;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "Button{" +
                "text='" + text + '\'' +
                ", active=" + active +
                ", font=" + font +
                ", tooltip='" + tooltip + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }
}
