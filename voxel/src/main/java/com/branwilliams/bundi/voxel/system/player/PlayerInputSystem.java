package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.components.*;
import com.branwilliams.bundi.voxel.VoxelScene;
import org.joml.Vector3f;

/**
 * Reads the player input from their keycodes and updates the movement component.
 * @author Brandon
 * @since August 11, 2019
 */
public class PlayerInputSystem extends AbstractSystem implements MouseListener, KeyListener {

    private final VoxelScene scene;

    private int selectedIdx = 0;

    public PlayerInputSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(Transformable.class, CameraComponent.class, MovementComponent.class,
                WalkComponent.class, PlayerState.class, PlayerControls.class));
        this.scene = scene;
        this.scene.addMouseListener(this);
        scene.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
            WalkComponent walkComponent = entity.getComponent(WalkComponent.class);
            PlayerControls playerControls = entity.getComponent(PlayerControls.class);
            PlayerState playerState = entity.getComponent(PlayerState.class);

            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
            Camera camera = cameraComponent.getCamera();

            float forwardSpeed = 0F, strafeSpeed = 0F, upwardSpeed = 0F;

            if (engine.getWindow().isKeyPressed(playerControls.getForward())) {
                forwardSpeed += 1F;
            }
            if (engine.getWindow().isKeyPressed(playerControls.getBackward())) {
                forwardSpeed += -1F;
            }

            if (engine.getWindow().isKeyPressed(playerControls.getLeft())) {
                strafeSpeed = -1F;
            }
            if (engine.getWindow().isKeyPressed(playerControls.getRight())) {
                strafeSpeed = 1F;
            }

//            if (engine.getWindow().isKeyPressed(playerControls.getAscend())) {
//                upwardSpeed = 1F;
//            }
//            if (engine.getWindow().isKeyPressed(playerControls.getDescend())) {
//                upwardSpeed = -1F;
//            }

            if (engine.getWindow().isKeyPressed(playerControls.getAscend()) && playerState.isOnGround()) {
                upwardSpeed = 30F;
                playerState.setOnGround(false);
            }

            if (engine.getWindow().isKeyPressed(playerControls.getUpdateSun())) {
                scene.getSun().setDirection(scene.getCamera().getFacingDirection().negate());
            }

            forwardSpeed *= walkComponent.getAccelerationFactor();
            strafeSpeed  *= walkComponent.getAccelerationFactor();
            upwardSpeed  *= walkComponent.getAccelerationFactor();

//            float dx = 0, dz = 0;
//            dx += camera.getFront().x * forwardSpeed;
//            dz +=  camera.getFront().z * forwardSpeed;
//
//            Vector3f frontCrossUp = new Vector3f(camera.getFront()).cross(camera.getUp());
//            dx += frontCrossUp.x * strafeSpeed;
//            dz += frontCrossUp.z * strafeSpeed;

            float yaw = camera.getYaw() + 90;
            float dx = Mathf.sin(Mathf.toRadians(yaw)) * forwardSpeed;
            float dz = Mathf.cos(Mathf.toRadians(yaw)) * -1F * forwardSpeed;
            dx += Mathf.sin(Mathf.toRadians(yaw - 90)) * -1F * strafeSpeed;
            dz += Mathf.cos(Mathf.toRadians(yaw - 90)) * strafeSpeed;

            movementComponent.getAcceleration().x = dx;
            movementComponent.getAcceleration().y = upwardSpeed;
            movementComponent.getAcceleration().z = dz;

//            System.out.println("accelX=" + movementComponent.getAcceleration().x
//                    + ", accelY=" + movementComponent.getAcceleration().y
//                    + ", accelZ=" + movementComponent.getAcceleration().z);

        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {

    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {

    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {

    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        if (scene.isLocked())
            return;

        if (yoffset > 0) {
            scene.getPlayerState().getInventory().nextItem();
        } else {
            scene.getPlayerState().getInventory().prevItem();
        }
//        selectedIdx += (yoffset > 0 ? 1 : -1);
//
//        List<String> sortedVoxelIdentifiers = scene.getVoxelRegistry().getSortedVoxelIdentifiers();
//
//        if (selectedIdx >= sortedVoxelIdentifiers.size()) {
//            selectedIdx = 0;
//        }
//        if (selectedIdx < 0) {
//            selectedIdx = sortedVoxelIdentifiers.size() - 1;
//        }
//
//        String voxelIdentifier = sortedVoxelIdentifiers.get(selectedIdx);
//
//        scene.getPlayerState().setVoxelInHand(scene.getVoxelRegistry().getVoxel(voxelIdentifier));
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        for (IEntity entity : getEs().getEntities(this)) {
            PlayerControls playerControls = entity.getComponent(PlayerControls.class);
            PlayerState playerState = entity.getComponent(PlayerState.class);
            if (playerControls.getNoclip().getKeyCode() == key) {
                playerState.setNoClip(!playerState.isNoClip());
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}
