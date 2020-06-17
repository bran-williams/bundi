package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Joystick;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Transformable;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class DebugCameraMoveSystem extends MouseControlSystem implements Window.JoystickListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Supplier<Camera> camera;

    private final float rotationSpeed;

    private final float moveSpeed;

    private final float joystickLookSpeed = 4F;

    private final float minJoystickAxisInput = 0.1F;

    private boolean alwaysRotate;

    private boolean rotating = false;

    private Joystick joystick;

    public DebugCameraMoveSystem(Scene scene, Supplier<Camera> camera, float rotationSpeed, float moveSpeed) {
        this(scene, Lockable.unlocked(), camera, rotationSpeed, moveSpeed, false);
    }

    public DebugCameraMoveSystem(Scene scene, Lockable lockable, Supplier<Camera> camera, float rotationSpeed, float moveSpeed,
                                 boolean alwaysRotate) {
        super(scene, lockable, new ClassComponentMatcher(Transformable.class));
        scene.addJoystickListener(this);
        this.camera = camera;
        this.rotationSpeed = rotationSpeed;
        this.moveSpeed = moveSpeed;
        this.alwaysRotate = alwaysRotate;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        super.init(engine, entitySystemManager, window);
        if (!window.getConnectedJoysticks().isEmpty()) {
            joystick = window.getConnectedJoysticks().get(0);
        }
    }

    @Override
    protected void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval, float mouseX, float mouseY, float oldMouseX, float oldMouseY) {
        if (rotating || alwaysRotate) {
            float dYaw = (mouseX - oldMouseX) * rotationSpeed;
            float dPitch = -(mouseY - oldMouseY) * rotationSpeed;
            camera.get().rotate(dYaw, dPitch);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
        if (!getLockable().isLocked() && buttonId == 0 && !alwaysRotate) {
            rotating = true;
            window.disableCursor();
        }
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
        if (!getLockable().isLocked() && buttonId == 0 && !alwaysRotate) {
            rotating = false;
            window.showCursor();
            window.centerCursor();
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        super.fixedUpdate(engine, entitySystemManager, deltaTime);
        if (getLockable().isLocked())
            return;

        if (!doJoystickMovement(engine, entitySystemManager, deltaTime)) {
            doWASDMovement(engine, entitySystemManager, deltaTime);
        }
    }

    private boolean doJoystickMovement(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (joystick == null)
            return false;

        joystick.updateGamepadState();

        float moveSpeed = this.moveSpeed * (float) deltaTime;
        Camera camera = this.camera.get();

        Vector2f leftAxis = joystick.getLeftAxis();

        boolean leftXMoved = Math.abs(leftAxis.x) >= minJoystickAxisInput;
        boolean leftYMoved = Math.abs(leftAxis.y) >= minJoystickAxisInput;

        boolean moved = leftXMoved || leftYMoved;

        float strafe = leftXMoved ? leftAxis.x * moveSpeed : 0F;
        float forward = leftYMoved ? -leftAxis.y * moveSpeed : 0F;

        camera.moveDirection(forward, strafe);

        Vector2f rightAxis = joystick.getRightAxis();

        boolean rightXMoved = Math.abs(rightAxis.x) >= minJoystickAxisInput;
        boolean rightYMoved = Math.abs(rightAxis.y) >= minJoystickAxisInput;

        moved = rightXMoved || rightYMoved || moved;

        float yaw = rightXMoved ? rightAxis.x : 0F;
        float pitch = rightYMoved ? -rightAxis.y : 0F;

        camera.rotate(yaw * joystickLookSpeed, pitch * joystickLookSpeed);

        if (joystick.isButtonPressed(Joystick.JoystickButton.A)) {
            camera.move(0, moveSpeed, 0F);
            moved = true;
        }

        if (joystick.isButtonPressed(Joystick.JoystickButton.B)) {
            camera.move(0, -moveSpeed, 0F);
            moved = true;
        }

        if (joystick.isButtonPressed(Joystick.JoystickButton.DPAD_UP)) {
            camera.moveDirection(moveSpeed, 0F);
            moved = true;
        }

        if (joystick.isButtonPressed(Joystick.JoystickButton.DPAD_DOWN)) {
            camera.moveDirection(-moveSpeed, 0F);
            moved = true;
        }

        if (joystick.isButtonPressed(Joystick.JoystickButton.DPAD_LEFT)) {
            camera.moveDirection(0F, -moveSpeed);
            moved = true;
        }

        if (joystick.isButtonPressed(Joystick.JoystickButton.DPAD_RIGHT)) {
            camera.moveDirection(0F, moveSpeed);
            moved = true;
        }

        return moved;
    }

    private void doWASDMovement(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
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
            camera.moveDirection(0F, -moveSpeed);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_D)) {
            camera.moveDirection(0F, moveSpeed);
        }
    }

    public boolean isRotating() {
        return rotating;
    }

    @Override
    public void onJoystickConnected(Joystick joystick) {
        if (this.joystick == null) {
            this.joystick = joystick;
            log.info("Joystick " + joystick.getName() + " connected! Using for debug camera.");
        }
    }

    @Override
    public void onJoystickDisconnected(Joystick joystick) {
        if (this.joystick != null && this.joystick.equals(joystick)) {
            this.joystick = null;
            log.info("Joystick disconnected, no longer using for debug camera.");
        }
    }
}
