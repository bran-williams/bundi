package com.branwilliams.bundi.engine.core.window;

import com.branwilliams.bundi.engine.core.Joystick;

/**
 * Listens for connecting and disconnecting joysticks.
 * */
public interface JoystickListener {

    void onJoystickConnected(Joystick joystick);

    void onJoystickDisconnected(Joystick joystick);
}
