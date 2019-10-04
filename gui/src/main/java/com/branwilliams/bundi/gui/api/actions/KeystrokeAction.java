package com.branwilliams.bundi.gui.api.actions;

/**
 * Keystroke action is passed to components when a key is pressed and released.<br/>
 * Created by Brandon Williams on 2/10/2017.
 */
public class KeystrokeAction {

    public final int key, scancode, mods;

    public KeystrokeAction(int key, int scancode, int mods) {
        this.key = key;
        this.scancode = scancode;
        this.mods = mods;
    }

    /**
     * Listener for the keystroke action.
     * */
    public interface KeystrokeActionListener extends ActionListener<KeystrokeAction> {

    }
}
