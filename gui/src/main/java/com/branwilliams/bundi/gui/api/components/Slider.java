package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

import java.util.function.Consumer;

/**
 * Simple slider implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Slider extends Component {

    private int barSize;

    private float sliderPercentage = 0F;

    private boolean sliding = false;

    private Consumer<Slider> valueChangeFunction;

    public Slider() {
        this(8);
    }

    public Slider(int barSize) {
        super();
        this.barSize = barSize;
        this.addListener(ClickEvent.class, (ClickEvent.ClickActionListener) action -> {
            switch (action.mouseClickAction) {
                case MOUSE_PRESS:
                    if (isHovered() && isPointInside(action.x, action.y) && action.buttonId == 0) {
                        sliding = true;
                        return true;
                    }
                    return false;
                case MOUSE_RELEASE:
                    if (sliding && valueChangeFunction != null) {
                        valueChangeFunction.accept(this);
                    }
                    sliding = false;
                    return false;
            }
           return false;
        });
    }

    @Override
    public void update() {
        updateSliding();
    }

    /**
     * Updates the slider percentage.
     * */
    public void updateSliding() {
        if (this.sliding) {
            float oldPercentage = this.sliderPercentage;
            this.sliderPercentage = Mathf.clamp((toolbox.getMouseX() - getX() - (getBarSize() / 2F)) / (getWidthForSlider()),
                    0F, 1F);
            if (oldPercentage != sliderPercentage && valueChangeFunction != null) {
                this.valueChangeFunction.accept(this);
            }
        } else
            this.sliding = false;
    }

    /**
     * @return The area of the bar used by this slider.
     * */
    public int[] getSliderBar() {
        return new int[] { getX(), getY(), (int) (sliderPercentage * getWidth()), getHeight() };
        // return new int[] { getX() + (int) (sliderPercentage * getWidthForSlider()), getY(), getBarSize(), getHeight() };
    }

    /**
     * @return True if the given point is within the slider bar.
     * */
    public boolean isPointInsideSlider(int x, int y) {
        return toolbox.isPointInside(x, y, getSliderBar());
    }

    /**
     * @return The total width for the slider.
     * */
    public float getWidthForSlider() {
        int SLIDER_PADDING = 0;
        float maxPointForRendering = (float) (getWidth() - getBarSize() - SLIDER_PADDING),
                beginPoint = (SLIDER_PADDING);
        return maxPointForRendering - beginPoint;
    }

    /**
     * Invokes the given function when this slider's value changes.
     * */
    public void onValueChange(Consumer<Slider> valueChangeFunction) {
        this.valueChangeFunction = valueChangeFunction;
    }

    public boolean isSliding() {
        return sliding;
    }

    public int getBarSize() {
        return barSize;
    }

    public void setBarSize(int barSize) {
        this.barSize = barSize;
    }

    public float getSliderPercentage() {
        return sliderPercentage;
    }

    public void setSliderPercentage(float sliderPercentage) {
        // store old one
        float oldSliderPercentage = this.sliderPercentage;
        // update THIS one
        this.sliderPercentage = sliderPercentage;

        // if change make function happy
        if (this.valueChangeFunction != null && oldSliderPercentage != this.sliderPercentage) {
            this.valueChangeFunction.accept(this);
        }
    }

    public int getFormattedValue() {
        return Mathf.clamp((int) (sliderPercentage * 100), 0, 100);
    }
}
