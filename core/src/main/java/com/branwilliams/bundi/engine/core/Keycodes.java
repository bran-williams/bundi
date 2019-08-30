package com.branwilliams.bundi.engine.core;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * @author Brandon
 * @since August 15, 2019
 */
public class Keycodes {

    private final Map<String, Integer> keyCodes;

    public Keycodes(Map<String, Integer> keyCodes) {
        this.keyCodes = keyCodes;
    }

    public int getKeycode(String key) {
        return keyCodes.getOrDefault(key, GLFW_KEY_UNKNOWN);
    }
}
