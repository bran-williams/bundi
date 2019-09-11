package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.systems.MouseControlSystem;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class DebugCameraMoveSystem extends MouseControlSystem {

    private final Supplier<Camera> camera;

    private final float rotationSpeed;

    private final float moveSpeed;

    private boolean rotating = false;

    public DebugCameraMoveSystem(Scene scene, Supplier<Camera> camera, float rotationSpeed, float moveSpeed) {
        super(scene, new ClassComponentMatcher(Transformable.class));
        this.camera = camera;
        this.rotationSpeed = rotationSpeed;
        this.moveSpeed = moveSpeed;
    }

    @Override
    protected void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval, float mouseX, float mouseY, float oldMouseX, float oldMouseY) {
        if (rotating) {
            camera.get().rotate((mouseY - oldMouseY) * rotationSpeed, (mouseX - oldMouseX) * rotationSpeed, 0F);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
        if (buttonId == 0) {
            rotating = true;
            window.disableCursor();
        }
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
        if (buttonId == 0) {
            rotating = false;
            window.showCursor();
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        super.fixedUpdate(engine, entitySystemManager, deltaTime);
        float moveSpeed = this.moveSpeed * (float) deltaTime;
        Camera camera = this.camera.get();

        if (engine.getWindow().isKeyPressed(GLFW_KEY_SPACE)) {
            camera.move(0, moveSpeed, 0F);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.move(0, -moveSpeed, 0F);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_W)) {
            camera.moveDirection(moveSpeed, 0F);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_S)) {
            camera.moveDirection(-moveSpeed, 0F);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_A)) {
            camera.moveDirection(0F, moveSpeed);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_D)) {
            camera.moveDirection(0F, -moveSpeed);
        }
    }
}
