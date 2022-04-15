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
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import org.joml.Vector3f;

/**
 * Reads the player input from their keycodes and updates the movement component.
 * @author Brandon
 * @since August 11, 2019
 */
public class PlayerInputSystem extends AbstractSystem implements MouseListener, KeyListener {

    private static final double DURATION_OF_JUMP = 0.2F;
    private static final float PLAYER_JUMP_HEIGHT = 1.2F;

    private final VoxelScene scene;

    private double jumpTime;


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

            if (engine.getWindow().isKeyPressed(playerControls.getAscend())) {
                upwardSpeed = jump(engine, playerState, transformable);
            }

            if (engine.getWindow().isKeyPressed(playerControls.getUpdateSun())) {
                scene.getSun().setDirection(scene.getCamera().getFacingDirection().negate());
            }

            forwardSpeed *= walkComponent.getAccelerationFactor();
            strafeSpeed  *= walkComponent.getAccelerationFactor();
            upwardSpeed  *= walkComponent.getAccelerationFactor();

            float dx = 0, dz = 0;

            if (PlayerState.CameraType.FREE.equals(playerState.getCameraType())) {
                dx += camera.getFront().x * forwardSpeed;
                dz +=  camera.getFront().z * forwardSpeed;

                Vector3f frontCrossUp = new Vector3f(camera.getFront()).cross(camera.getUp());
                dx += frontCrossUp.x * strafeSpeed;
                dz += frontCrossUp.z * strafeSpeed;
            } else {
                float yaw = camera.getYaw() + 90;
                dx = Mathf.sin(Mathf.toRadians(yaw)) * forwardSpeed;
                dz = Mathf.cos(Mathf.toRadians(yaw)) * -1F * forwardSpeed;
                dx += Mathf.sin(Mathf.toRadians(yaw - 90)) * -1F * strafeSpeed;
                dz += Mathf.cos(Mathf.toRadians(yaw - 90)) * strafeSpeed;
            }

//            if (dx > 0 || dx < 0)
            movementComponent.getAcceleration().x = dx;
            movementComponent.getAcceleration().y = upwardSpeed;
//            if (dz > 0 || dz < 0)
            movementComponent.getAcceleration().z = dz;
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
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        for (IEntity entity : getEs().getEntities(this)) {
            PlayerControls playerControls = entity.getComponent(PlayerControls.class);
            PlayerState playerState = entity.getComponent(PlayerState.class);
            if (playerControls.getNoclip().getKeyCode() == key) {
                playerState.setNoClip(!playerState.isNoClip());
                if (playerState.isNoClip()) {
                    playerState.setCameraType(PlayerState.CameraType.FREE);
                } else {
                    playerState.setCameraType(PlayerState.CameraType.FIRST_PERSON);
                }
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

    private float jump(Engine engine, PlayerState playerState, Transformable transformable) {
        if (playerState.isOnGround()) {
            playerState.setOnGround(false);
            playerState.setInitialJumpHeight(transformable.getPosition().y);

            this.jumpTime = engine.getTime();
//            System.out.println("Player starts jumping!");
            return getJumpImpulse(PLAYER_JUMP_HEIGHT, 9.8F, playerState.getMass());
        } else if (engine.getTime() - this.jumpTime <= DURATION_OF_JUMP) {
            double t = engine.getTime() - this.jumpTime;
            float dist = Math.max(0, (transformable.getPosition().y - playerState.getInitialJumpHeight()));
//            System.out.println("Player jumping for " + t);
            return getJumpForce(dist, 9.8F, playerState.getMass(), (float) t);
        }
        return 0;
    }

    private float getJumpImpulse(float dist, float g, float mass) {
        float v = Mathf.sqrt(2 * g * dist);
        return v / mass;
    }

    private float getJumpForce(float dist, float g, float mass, float t) {
        // t is the total time the jump force can be applied, in seconds
        float v = Mathf.sqrt(2 * g * dist);
        float a = t * t;
        float b = 2 * v * t - g * t * t;
        float c = - 2 * g * dist;
        return ((-b + Mathf.sqrt(b*b-4*a*c)) / (2*a)) / mass;
    }
}
