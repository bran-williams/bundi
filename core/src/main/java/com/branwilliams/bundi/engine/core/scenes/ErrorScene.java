package com.branwilliams.bundi.engine.core.scenes;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.context.Ignore;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;

/**
 * Created by Brandon Williams on 2/2/2018.
 */
@Ignore
public class ErrorScene extends AbstractScene implements MouseListener {

    private Transformable mouseOffset = new Transformation();

    private boolean movingMouse;

    public ErrorScene(Exception exception) {
        super("error_scene");
        this.addMouseListener(this);
        this.setRenderer(new ErrorSceneRenderer(this::getMouseOffset, exception));
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
    }

    @Override
    public void play(Engine engine) {
        engine.getWindow().showCursor();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public  boolean destroyUponReplacement() {
        return true;
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        if (movingMouse) {
            mouseOffset.move(newMouseX - oldMouseX, newMouseY - oldMouseX, 0F);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        movingMouse = true;
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        movingMouse = false;
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {

    }

    public Transformable getMouseOffset() {
        return mouseOffset;
    }
}
