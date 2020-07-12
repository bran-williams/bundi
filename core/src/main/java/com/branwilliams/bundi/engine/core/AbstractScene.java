package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.context.Ignore;
import com.branwilliams.bundi.engine.core.screenshot.ScreenshotCapturer;
import com.branwilliams.bundi.engine.core.window.*;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.tukio.EventManager;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;

/**
 * Basic implementation of the {@link Scene} interface.
 * Created by Brandon Williams on 1/4/2018.
 */
@Ignore
public abstract class AbstractScene extends AbstractWindowEventListener implements Scene {

    public static final int KEY_SCREENSHOT = GLFW_KEY_F9;

    private final String name;

    protected ScreenshotCapturer screenshotCapturer;

    protected EntitySystemManager es = new EntitySystemManager();

    protected EventManager eventManager = new EventManager();

    protected Renderer renderer;

    public AbstractScene(String name) {
        this(name, null);
    }

    public AbstractScene(String name, Renderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        screenshotCapturer = new ScreenshotCapturer(engine.getContext());
        this.addKeyListener(screenshotCapturer.screenshotOnKeyPress(KEY_SCREENSHOT));
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
