package com.branwilliams.bundi.engine.core;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

/**
 * @author Brandon
 * @since August 15, 2019
 */
public class Keycodes {

    private final Map<String, Integer> keyCodes;

    private final Map<Integer, String> keyNames;

    public Keycodes(Map<String, Integer> keyCodes) {
        this.keyCodes = keyCodes;

        this.keyNames = new HashMap<>();
        for (Map.Entry<String, Integer> entry : keyCodes.entrySet()) {
            keyNames.put(entry.getValue(), entry.getKey());
        }
    }

    public int getKeycode(String key) {
        return keyCodes.getOrDefault(key, GLFW_KEY_UNKNOWN);
    }

    public String getKeyName(int keyCode) {
        return keyNames.getOrDefault(keyCode, null);
    }
}
