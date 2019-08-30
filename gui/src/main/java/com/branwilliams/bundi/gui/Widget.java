package com.branwilliams.bundi.gui;

import com.branwilliams.bundi.engine.core.Destructible;

/**
 * Contains an x, y, width, and height. <br/>
 * Created by Brandon Williams on 2/28/2017.
 */
public interface Widget extends Destructible {

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);


    /**
     * Set the x and y position of this component.
     * */
    default void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * Set the width and height of this component.
     * */
    default void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Width and height will be adjusted by the scale factor provided.
     * */
    default void scale(float scale) {
        this.setWidth((int) (getWidth() * scale));
        this.setHeight((int) (getHeight() * scale));
    }

    /**
     * @return True if the given x and y position is within this dimension.
     * */
    boolean isPointInside(int x, int y);

    /**
     * @return An int array containing the x, y, width, and height in that order.
     * */
    int[] getArea();

    default void destroy() {

    }
}
