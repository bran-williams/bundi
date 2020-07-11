package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.core.Lockable;

/**
 * Created by Brandon Williams on 12/24/2018.
 */
public abstract class MouseControlSystem extends AbstractSystem implements MouseListener {

    private float oldMouseX;
    private float oldMouseY;

    // True when the mouse positions change. Does not consider the first mouse positions.
    private boolean mouseMoved = false;

    // True to preserve the first mouse positions given.
    private boolean firstUpdate = true;

    /** Controls when the mouseMove function will be invoked. When this object is set to 'locked', the firstUpdate field
     *  will be reset to true. */
    private Lockable lockable;

    /**
     * Constructs a mouse control system which does not depend on a lockable object.
     * */
    public MouseControlSystem(Scene scene, IComponentMatcher componentMatcher) {
        this(scene, Lockable.unlocked(), componentMatcher);
    }

    public MouseControlSystem(Scene scene, Lockable lockable, IComponentMatcher componentMatcher) {
        super(componentMatcher);
        this.lockable = lockable;
        scene.addMouseListener(this);
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (lockable.isLocked()) {
            firstUpdate = true;
        } else if (mouseMoved) {
            mouseMoved = false;
            float mouseX = engine.getWindow().getMouseX();
            float mouseY = engine.getWindow().getMouseY();
            mouseMove(engine, entitySystemManager, deltaTime, mouseX, mouseY, oldMouseX, oldMouseY);
            oldMouseX = mouseX;
            oldMouseY = mouseY;
        }
    }

    /**
     * Invoked whenever the mouse moves. This is invoked under the update function.
     * */
    protected abstract void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval,
                                      float mouseX, float mouseY, float oldMouseX, float oldMouseY);

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        if (firstUpdate) {
            this.oldMouseX = newMouseX;
            this.oldMouseY = newMouseY;
            firstUpdate = false;
        } else {
            mouseMoved = true;
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {

    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {

    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {

    }

    public Lockable getLockable() {
        return lockable;
    }

    public void setLockable(Lockable lockable) {
        this.lockable = lockable;
    }
}
