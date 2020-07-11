package com.branwilliams.bundi.engine.core.window;

/**
 * Listens for key presses/releases within a window.
 * */
public interface KeyListener {
    /**
     * Invoked when a key is pressed.
     * */
    void keyPress(Window window, int key, int scancode, int mods);

    /**
     * Invoked when a key is released.
     * */
    void keyRelease(Window window, int key, int scancode, int mods);

    /**
     * Invoked when a key has been held down.
     * */
    default void keyHeld(Window window, int key, int scancode, int mods) {}
}
