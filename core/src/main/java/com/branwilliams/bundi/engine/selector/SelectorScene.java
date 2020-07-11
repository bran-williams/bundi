package com.branwilliams.bundi.engine.selector;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.context.Ignore;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since February 26, 2019
 */
@Ignore
public class SelectorScene extends AbstractScene implements KeyListener {

    private Engine engine;

    private int selected = 0;

    private List<Class<? extends Scene>> scenes = new ArrayList<>();

    public SelectorScene() {
        super("selector_scene");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        this.engine = engine;
        setRenderer(new SelectorRenderer(this, window));
        addKeyListener(this);
        this.scenes = new ArrayList<>(engine.getContext().getScenes());
        this.scenes.removeIf(this.getClass()::equals);
    }

    @Override
    public void play(Engine engine) {

    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        switch (key) {
            case GLFW_KEY_UP:
                selected--;
                if (selected <= 0) {
                    selected = 0;
                }
                break;
            case GLFW_KEY_DOWN:
                selected++;
                if (selected >= scenes.size()) {
                    selected = scenes.size() - 1;
                }
                break;
            case GLFW_KEY_ENTER:
                try {
                    Scene scene = scenes.get(selected).newInstance();
                    engine.setScene(scene);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new NullPointerException("Unable to create scene " + scenes.get(selected).getSimpleName());
                }
                break;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

    @Override
    public boolean destroyUponReplacement() {
        return true;
    }

    public int getSelected() {
        return selected;
    }

    public List<Class<? extends Scene>> getScenes() {
        return scenes;
    }
}
