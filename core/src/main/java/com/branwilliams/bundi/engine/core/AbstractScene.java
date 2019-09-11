package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.context.Ignore;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.tukio.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the {@link Scene} interface.
 * Created by Brandon Williams on 1/4/2018.
 */
@Ignore
public abstract class AbstractScene implements Scene {

    private final String name;

    protected EntitySystemManager es = new EntitySystemManager();

    protected EventManager eventManager = new EventManager();

    protected Renderer renderer;

    // Every listener for the current window is stored within the current scene.
    private final List<Window.WindowListener> windowListeners = new ArrayList<>();
    private final List<Window.MouseListener> mouseListeners = new ArrayList<>();
    private final List<Window.KeyListener> keyListeners = new ArrayList<>();
    private final List<Window.CharacterListener> characterListeners = new ArrayList<>();

    public AbstractScene(String name) {
        this(name, null);
    }

    public AbstractScene(String name, Renderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    @Override
    public void update(Engine engine, double deltaTime) {
        es.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        es.fixedUpdate(engine, deltaTime);
    }

    @Override
    public void destroy() {
        es.destroy();

        if (renderer != null)
            renderer.destroy();
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public List<Window.WindowListener> getWindowListeners() {
        return windowListeners;
    }

    @Override
    public void addWindowListener(Window.WindowListener windowListener) {
        this.windowListeners.add(windowListener);
    }

    @Override
    public boolean removeWindowListener(Window.WindowListener windowListener) {
        return windowListeners.remove(windowListener);
    }

    @Override
    public List<Window.MouseListener> getMouseListeners() {
        return mouseListeners;
    }

    @Override
    public void addMouseListener(Window.MouseListener mouseListener) {
        this.mouseListeners.add(mouseListener);
    }

    @Override
    public boolean removeMouseListener(Window.MouseListener mouseListener) {
        return mouseListeners.remove(mouseListener);
    }


    @Override
    public List<Window.KeyListener> getKeyListeners() {
        return keyListeners;
    }

    @Override
    public void addKeyListener(Window.KeyListener keyListener) {
        this.keyListeners.add(keyListener);
    }

    @Override
    public boolean removeKeyListener(Window.KeyListener keyListener) {
        return keyListeners.remove(keyListener);
    }

    @Override
    public List<Window.CharacterListener> getCharacterListeners() {
        return characterListeners;
    }

    @Override
    public void addCharacterListener(Window.CharacterListener characterListener) {
        this.characterListeners.add(characterListener);
    }

    @Override
    public boolean removeCharacterListener(Window.CharacterListener characterListener) {
        return characterListeners.remove(characterListener);
    }

    @Override
    public EntitySystemManager getEs() {
        return es;
    }

    @Override
    public void setEs(EntitySystemManager es) {
        this.es = es;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public String toString() {
        return "["
                + getName()
                + "]";
    }
}
