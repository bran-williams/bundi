package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickAction;

import java.util.function.BiFunction;

/**
 * Simple button implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Button extends Component {

    private String text;

    private boolean highlight;

    private boolean pressed = false;

    private BiFunction<Button, ClickAction, Boolean> pressFunction;

    public Button(String tag, String text) {
        this(tag, text, false);
    }

    public Button(String tag, String text, boolean highlight) {
        super(tag);
        this.text = text;
        this.highlight = highlight;
        this.addListener(Actions.MOUSE_PRESS, (ClickAction.ClickActionListener) action -> {
            if (isHovered() && isPointInside(action.x, action.y)) {
                pressed = true;
                return true;
            }
            return false;
        });
        this.addListener(Actions.MOUSE_RELEASE, (ClickAction.ClickActionListener) action -> {
            // return true only if mouse release on top of button and button was pressed when initially clicked.
            if (pressed && isPointInside(action.x, action.y)) {
                if (pressFunction != null) {
                    pressFunction.apply(this, action);
                }
                pressed = false;
                return true;
            }
            pressed = false;
            return false;
        });
    }

    @Override
    public void update() {

    }

    /**
     * Invokes the given function when this button is pressed.
     * */
    public void onPressed(BiFunction<Button, ClickAction, Boolean> pressFunction) {
        this.pressFunction = pressFunction;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean isPressed() {
        return pressed;
    }

    @Override
    public String toString() {
        return "Button{" +
                "text='" + text + '\'' +
                ", highlight=" + highlight +
                ", font=" + font +
                ", tooltip='" + tooltip + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }
}
