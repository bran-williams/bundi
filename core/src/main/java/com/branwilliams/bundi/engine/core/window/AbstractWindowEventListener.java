package com.branwilliams.bundi.engine.core.window;

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
    public List<WindowListener> getWindowListeners() {
        return windowListeners;
    }

    @Override
    public void addWindowListener(WindowListener windowListener) {
        windowListeners.add(windowListener);
    }

    @Override
    public boolean removeWindowListener(WindowListener windowListener) {
        return windowListeners.remove(windowListener);
    }

    @Override
    public List<MouseListener> getMouseListeners() {
        return mouseListeners;
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        this.mouseListeners.add(mouseListener);
    }

    @Override
    public boolean removeMouseListener(MouseListener mouseListener) {
        return mouseListeners.remove(mouseListener);
    }

    @Override
    public List<KeyListener> getKeyListeners() {
        return keyListeners;
    }

    @Override
    public void addKeyListener(KeyListener keyListener) {
        this.keyListeners.add(keyListener);
    }

    @Override
    public boolean removeKeyListener(KeyListener keyListener) {
        return keyListeners.remove(keyListener);
    }

    @Override
    public List<CharacterListener> getCharacterListeners() {
        return characterListeners;
    }

    @Override
    public void addCharacterListener(CharacterListener characterListener) {
        this.characterListeners.add(characterListener);
    }

    @Override
    public boolean removeCharacterListener(CharacterListener characterListener) {
        return characterListeners.remove(characterListener);
    }

    @Override
    public List<JoystickListener> getJoystickListeners() {
        return joystickListeners;
    }

    @Override
    public void addJoystickListener(JoystickListener joystickListener) {
        this.joystickListeners.add(joystickListener);
    }

    @Override
    public boolean removeJoystickListener(JoystickListener joystickListener) {
        return joystickListeners.remove(joystickListener);
    }
}
