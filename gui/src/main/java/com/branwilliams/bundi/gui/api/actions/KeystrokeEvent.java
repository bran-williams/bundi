package com.branwilliams.bundi.gui.api.actions;

import com.branwilliams.bundi.engine.core.Keystroke;

/**
 * Keystroke action is passed to components when a key is pressed and released.<br/>
 * Created by Brandon Williams on 2/10/2017.
 */
public class KeystrokeEvent {

    public enum KeystrokeAction {
        KEY_PRESS, KEY_RELEASE, KEY_HELD;
    }

    public final Keystroke keystroke;

    public final KeystrokeAction keystrokeAction;

    public KeystrokeEvent(int key, int scancode, int mods, KeystrokeAction keystrokeAction) {
        this.keystroke = new Keystroke(key, scancode, mods);
        this.keystrokeAction = keystrokeAction;
    }

    /**
     * Listener for the keystroke action.
     * */
    public interface KeystrokeActionListener extends ActionListener<KeystrokeEvent> {

    }
}
