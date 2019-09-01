package com.branwilliams.bundi.pbr.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.systems.MouseControlSystem;
import com.branwilliams.bundi.pbr.PbrScene;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 01, 2019
 */
public class TransformationRotationSystem extends MouseControlSystem {

    private final Supplier<Transformable> transformable;

    private final float rotationSpeed;

    private boolean rotating = false;

    public TransformationRotationSystem(Scene scene, Supplier<Transformable> transformable, float rotationSpeed) {
        super(scene, new ClassComponentMatcher(Transformable.class));
        this.transformable = transformable;
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    protected void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval,
                             float mouseX, float mouseY, float oldMouseX, float oldMouseY) {
        if (rotating) {
            Transformable transformable = this.transformable.get();
            transformable.getRotation().y += (mouseX - oldMouseX) * rotationSpeed;
            transformable.getRotation().x += (mouseY - oldMouseY) * rotationSpeed;
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
        rotating = true;
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
        rotating = false;
    }
}
