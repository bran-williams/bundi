package com.branwilliams.bundi.engine.core.window;

/**
 * Listens for characters being typed within a window.
 * */
public interface CharacterListener {
    /**
     * Invoked when a character is typed.
     * */
    void charTyped(Window window, String characters);
}
