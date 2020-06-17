package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

import java.util.function.BiFunction;

/**
 * Simple checkbox implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Checkbox extends Component {

    public static final int CHECKBOX_PADDING = 2;

    public static final int CHECKBOX_TEXT_PADDING = 2;

    private String text;

    private boolean enabled = false;

    private boolean pressed = false;

    private BiFunction<Checkbox, ClickEvent, Boolean> pressFunction;

    public Checkbox(String text) {
        this( text, false);
    }

    public Checkbox(String text, boolean enabled) {
        super();
        this.text = text;
        this.enabled = enabled;
        this.addListener(ClickEvent.class, (ClickEvent.ClickActionListener) event -> {
            switch (event.mouseClickAction) {
                case MOUSE_PRESS:
                    if (isHovered() && isPointInside(event.x, event.y) && event.buttonId == 0) {
                        pressed = true;
                        return true;
                    }
                    return false;

                case MOUSE_RELEASE:
                    if (pressed && isPointInside(event.x, event.y)) {
                        if (pressFunction != null) {
                            pressFunction.apply(this, event);
                        }
                        pressed = false;
                        onPressed();
                        return true;
                    }
                    pressed = false;
                    return false;

                default:
                    return false;
            }
        });

    }

    protected void onPressed() {}

    @Override
    public void update() {
        this.setWidth(getCheckboxSize() + font.getStringWidth(text) + CHECKBOX_TEXT_PADDING);
        this.setHeight(getCheckboxSize());
    }

    /**
     * Invokes the given function when this button is pressed.
     * */
    public void onPressed(BiFunction<Checkbox, ClickEvent, Boolean> pressFunction) {
        this.pressFunction = pressFunction;
    }
    /**
     * @return An int array of the dimensions of the check box.
     * */
    public int[] getCheckbox() {
        return new int[] { getX(), getY(), getCheckboxSize(), getCheckboxSize() };
    }

    public int getCheckboxSize() {
        return font.getFontHeight();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPressed() {
        return pressed;
    }

	@Override
    public void setFont(FontData font) {
        super.setFont(font);
        this.update();
    }
}
