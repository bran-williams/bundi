package com.branwilliams.bundi.gui;

import com.branwilliams.bundi.engine.core.Destructible;

/**
 * Simple implementation of the dimension interface. Used as a base 'rectangle' object for the gui.
 * */
public class BasicWidget implements Widget {

    private int x, y, width, height;

    public BasicWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public BasicWidget(int x, int y, int size) {
        this(x, y, size, size);
    }

    public BasicWidget() {
        this(0, 0, 0, 0);
    }

    /**
     * @return An int array containing the x, y, width, and height of this component.
     * */
    @Override
    public int[] getArea() {
        return new int[] { x, y, width, height };
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void scale(float scale) {
        width *= scale;
        height *= scale;
    }

    /**
     * @return True if the x and y position is considered within the component.
     * */
    @Override
    public boolean isPointInside(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    @Override
    public void destroy() {

    }
}
