package com.branwilliams.bundi.engine.core.window;

import com.branwilliams.bundi.engine.core.Joystick;

import java.util.ArrayList;
import java.util.List;

public class AbstractWindowEventListener implements WindowEventListener {

    private final List<WindowListener> windowListeners;

    private final List<MouseListener> mouseListeners;

    private final List<KeyListener> keyListeners;

    private final List<CharacterListener> characterListeners;

    private final List<JoystickListener> joystickListeners;

    public AbstractWindowEventListener() {
        windowListeners = new ArrayList<>();
        mouseListeners = new ArrayList<>();
        keyListeners = new ArrayList<>();
        characterListeners = new ArrayList<>();
        joystickListeners = new ArrayList<>();
    }

    @Override
    public void resize(Window window, int width, int height) {
        for (WindowListener windowListener : windowListeners) {
            windowListener.resize(window, width, height);
        }
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.move(window, newMouseX, newMouseY, oldMouseX, oldMouseY);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.press(window, mouseX, mouseY, buttonId);
        }
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.release(window, mouseX, mouseY, buttonId);
        }
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        for (MouseListener mouseListener : mouseListeners) {
            mouseListener.wheel(window, xoffset, yoffset);
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        for (KeyListener keyListener : keyListeners) {
            keyListener.keyPress(window, key, scancode, mods);
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        for (KeyListener keyListener : keyListeners) {
            keyListener.keyRelease(window, key, scancode, mods);
        }
    }

    @Override
    public void charTyped(Window window, String characters) {
        for (CharacterListener characterListener : characterListeners) {
            characterListener.charTyped(window, characters);
        }
    }

    @Override
    public void onJoystickConnected(Joystick joystick) {
        for (JoystickListener joystickListener : joystickListeners) {
            joystickListener.onJoystickConnected(joystick);
        }
    }

    @Override
    public void onJoystickDisconnected(Joystick joystick) {
        for (JoystickListener joystickListener : joystickListeners) {
            joystickListener.onJoystickDisconnected(joystick);
        }
    }

    public List<WindowListener> getWindowListeners() {
        return windowListeners;
    }

    public void addWindowListener(WindowListener windowListener) {
        windowListeners.add(windowListener);
    }

    public boolean removeWindowListener(WindowListener windowListener) {
        return windowListeners.remove(windowListener);
    }

    public List<MouseListener> getMouseListeners() {
        return mouseListeners;
    }

    public void addMouseListener(MouseListener mouseListener) {
        this.mouseListeners.add(mouseListener);
    }

    public boolean removeMouseListener(MouseListener mouseListener) {
        return mouseListeners.remove(mouseListener);
    }

    public List<KeyListener> getKeyListeners() {
        return keyListeners;
    }

    public void addKeyListener(KeyListener keyListener) {
        this.keyListeners.add(keyListener);
    }

    public boolean removeKeyListener(KeyListener keyListener) {
        return keyListeners.remove(keyListener);
    }

    public List<CharacterListener> getCharacterListeners() {
        return characterListeners;
    }

    public void addCharacterListener(CharacterListener characterListener) {
        this.characterListeners.add(characterListener);
    }

    public boolean removeCharacterListener(CharacterListener characterListener) {
        return characterListeners.remove(characterListener);
    }

    public List<JoystickListener> getJoystickListeners() {
        return joystickListeners;
    }

    public void addJoystickListener(JoystickListener joystickListener) {
        this.joystickListeners.add(joystickListener);
    }

    public boolean removeJoystickListener(JoystickListener joystickListener) {
        return joystickListeners.remove(joystickListener);
    }
}
