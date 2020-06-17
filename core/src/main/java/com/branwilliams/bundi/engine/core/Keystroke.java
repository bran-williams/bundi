package com.branwilliams.bundi.engine.core;

public class Keystroke {

    public final int key, scancode, mods;

    public Keystroke(int key, int scancode, int mods) {
        this.key = key;
        this.scancode = scancode;
        this.mods = mods;
    }
}
