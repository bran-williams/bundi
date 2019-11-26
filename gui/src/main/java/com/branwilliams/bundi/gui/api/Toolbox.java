package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.engine.core.Window;

/**
 * Simple utility class used within the components system to perform simple calculations. <br/>
 * Created by Brandon Williams on 2/4/2017.
 */
public interface Toolbox {

    Window getWindow();

    Engine getEngine();

    /**
     * @return The x position of the mouse.
     * */
    int getMouseX();

    /**
     * @return The y position of the mouse.
     * */
    int getMouseY();

    /**
     * @return The width of the screen.
     * */
    int getWidth();

    /**
     * @return The height of the screen.
     * */
    int getHeight();

    /**
     * @return The user's clipboard string.
     * */
    String getClipboard();

    /**
     * @return True if the x and y positions are within the rectangle specified by the int array in the form: x, y, width, height.
     * */
    boolean isPointInside(int x, int y, int[] rect);

    /**
     * @return An object from the given location.
     * */
    <O> O get(String location);

    /**
     * @return An object from the given location.
     * */
    <O> O get(Nameable location);

    /**
     * Puts the object into this toolbox with the given location.
     * */
    void put(String location, Object object);

    /**
     * Puts the object into this toolbox with the given location.
     * */
    void put(Nameable location, Object object);

    // <O extends Shape2f> O shape(Class<? extends O> shape);
}
