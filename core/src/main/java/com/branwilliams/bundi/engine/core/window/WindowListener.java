package com.branwilliams.bundi.engine.core.window;

/**
 * Listens for the resizing of a window.
 * */
public interface WindowListener {
    /**
     * Invoked when the window is resized.
     * */
    void resize(Window window, int width, int height);
}
