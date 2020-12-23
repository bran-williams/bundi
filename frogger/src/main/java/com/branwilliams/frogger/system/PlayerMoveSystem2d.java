package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Joystick;
import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.JoystickListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.systems.MouseControlSystem;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class PlayerMoveSystem2d extends MouseControlSystem implements JoystickListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Supplier<Vector2f> focalPoint;

    private final Consumer<Vector2f> focalPointSetter;

    private final Supplier<Vector2f> targetFocalPoint;

    private final float moveSpeed;

    private final float joystickLookSpeed = 4F;

    private final float minJoystickAxisInput = 0.1F;

    private Joystick joystick;

    public PlayerMoveSystem2d(Scene scene, Lockable lockable, Supplier<Vector2f> focalPoint,
                              Consumer<Vector2f> focalPointSetter, Supplier<Vector2f> targetFocalPoint,
                              float moveSpeed) {
        super(scene, lockable, new ClassComponentMatcher(Transformable.class));
        scene.addJoystickListener(this);
        this.focalPoint = focalPoint;
        this.focalPointSetter = focalPointSetter;
        this.targetFocalPoint = targetFocalPoint;
        this.moveSpeed = moveSpeed;
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
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        super.fixedUpdate(engine, entitySystemManager, deltaTime);
        if (getLockable().isLocked())
            return;

//        if (!doJoystickMovement(engine, entitySystemManager, deltaTime)) {
//            doWASDMovement(engine, entitySystemManager, deltaTime);
//        }
    }
//    protected boolean doJoystickMovement(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
//        if (joystick == null)
//            return false;
//
//        joystick.updateGamepadState();
//
//        float moveSpeed = this.moveSpeed * (float) deltaTime;
//        Camera camera = this.camera.get();
//
//        Vector2f leftAxis = joystick.getLeftAxis();
//
//        boolean leftXMoved = Math.abs(leftAxis.x) >= minJoystickAxisInput;
//        boolean leftYMoved = Math.abs(leftAxis.y) >= minJoystickAxisInput;
//
//        boolean moved = leftXMoved || leftYMoved;
//
//        float strafe = leftXMoved ? leftAxis.x * moveSpeed : 0F;
//        float forward = leftYMoved ? -leftAxis.y * moveSpeed : 0F;
//
//        camera.moveDirection(forward, strafe);
//
////        Vector2f rightAxis = joystick.getRightAxis();
////        boolean rightXMoved = Math.abs(rightAxis.x) >= minJoystickAxisInput;
////        boolean rightYMoved = Math.abs(rightAxis.y) >= minJoystickAxisInput;
////
////        moved = rightXMoved || rightYMoved || moved;
////
////        float yaw = rightXMoved ? rightAxis.x : 0F;
////        float pitch = rightYMoved ? -rightAxis.y : 0F;
////
////        camera.rotate(yaw * joystickLookSpeed, pitch * joystickLookSpeed);
//
//        if (joystick.isButtonPressed(Joystick.JoystickButton.A)) {
//            camera.move(0, moveSpeed, 0F);
//            moved = true;
//        }
//
//        if (joystick.isButtonPressed(Joystick.JoystickButton.B)) {
//            camera.move(0, -moveSpeed, 0F);
//            moved = true;
//        }
//
//        return moved;
//    }
//
//    protected void doWASDMovement(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
//        float moveSpeed = this.moveSpeed * (float) deltaTime;
//        Camera camera = this.camera.get();
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_SPACE)) {
//            camera.move(0, moveSpeed, 0F);
//        }
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
//            camera.move(0, -moveSpeed, 0F);
//        }
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_W)) {
//            camera.moveDirection(moveSpeed, 0F);
//        }
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_S)) {
//            camera.moveDirection(-moveSpeed, 0F);
//        }
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_A)) {
//            camera.moveDirection(0F, -moveSpeed);
//        }
//
//        if (engine.getWindow().isKeyPressed(GLFW_KEY_D)) {
//            camera.moveDirection(0F, moveSpeed);
//        }
//    }

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
