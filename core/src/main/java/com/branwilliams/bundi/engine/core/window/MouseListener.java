package com.branwilliams.bundi.engine.core.window;

/**
 * Listens for mouse movement, mouse button presses and
 * releases, and mouse wheels within a window.
 * */
public interface MouseListener {
    /**
     * Invoked when the mouse moves.
     * */
    void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY);

    /**
     * Invoked when a mouse button is pressed.
     * */
    void press(Window window, float mouseX, float mouseY, int buttonId);

    /**
     * Invoked when a mouse button is released.
     * */
    void release(Window window, float mouseX, float mouseY, int buttonId);

    /**
     * Invoked when the mouse wheel moves.
     * */
    void wheel(Window window, double xoffset, double yoffset);
}
