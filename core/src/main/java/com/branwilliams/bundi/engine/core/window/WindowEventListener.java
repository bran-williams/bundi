package com.branwilliams.bundi.engine.core.window;

import com.branwilliams.bundi.engine.core.Joystick;

import java.util.List;

public interface WindowEventListener extends WindowListener, MouseListener, KeyListener, CharacterListener,
        JoystickListener {

    @Override
    default void resize(Window window, int width, int height) {
        for (WindowListener windowListener : getWindowListeners()) {
            windowListener.resize(window, width, height);
        }
    }

    @Override
    default void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        for (MouseListener mouseListener : getMouseListeners()) {
            mouseListener.move(window, newMouseX, newMouseY, oldMouseX, oldMouseY);
        }
    }

    @Override
    default void press(Window window, float mouseX, float mouseY, int buttonId) {
        for (MouseListener mouseListener : getMouseListeners()) {
            mouseListener.press(window, mouseX, mouseY, buttonId);
        }
    }

    @Override
    default void release(Window window, float mouseX, float mouseY, int buttonId) {
        for (MouseListener mouseListener : getMouseListeners()) {
            mouseListener.release(window, mouseX, mouseY, buttonId);
        }
    }

    @Override
    default void wheel(Window window, double xoffset, double yoffset) {
        for (MouseListener mouseListener : getMouseListeners()) {
            mouseListener.wheel(window, xoffset, yoffset);
        }
    }

    @Override
    default void keyPress(Window window, int key, int scancode, int mods) {
        for (KeyListener keyListener : getKeyListeners()) {
            keyListener.keyPress(window, key, scancode, mods);
        }
    }

    @Override
    default void keyRelease(Window window, int key, int scancode, int mods) {
        for (KeyListener keyListener : getKeyListeners()) {
            keyListener.keyRelease(window, key, scancode, mods);
        }
    }

    @Override
    default void charTyped(Window window, String characters) {
        for (CharacterListener characterListener : getCharacterListeners()) {
            characterListener.charTyped(window, characters);
        }
    }

    @Override
    default void onJoystickConnected(Joystick joystick) {
        for (JoystickListener joystickListener : getJoystickListeners()) {
            joystickListener.onJoystickConnected(joystick);
        }
    }

    @Override
    default void onJoystickDisconnected(Joystick joystick) {
        for (JoystickListener joystickListener : getJoystickListeners()) {
            joystickListener.onJoystickDisconnected(joystick);
        }
    }

    List<WindowListener> getWindowListeners();

    void addWindowListener(WindowListener windowListener);

    boolean removeWindowListener(WindowListener windowListener);

    List<MouseListener> getMouseListeners();

    void addMouseListener(MouseListener mouseListener);

    boolean removeMouseListener(MouseListener mouseListener);

    List<KeyListener> getKeyListeners();

    void addKeyListener(KeyListener keyListener);

    boolean removeKeyListener(KeyListener keyListener);

    List<CharacterListener> getCharacterListeners();

    void addCharacterListener(CharacterListener characterListener);

    boolean removeCharacterListener(CharacterListener characterListener);

    List<JoystickListener> getJoystickListeners();

    void addJoystickListener(JoystickListener joystickListener);

    boolean removeJoystickListener(JoystickListener joystickListener);
}
