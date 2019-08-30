package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.components.MovementComponent;
import com.branwilliams.bundi.voxel.components.PlayerState;
import com.branwilliams.bundi.voxel.components.PlayerControls;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.WalkComponent;

import java.util.List;

/**
 * Reads the player input from their keycodes and updates the movement component.
 * @author Brandon
 * @since August 11, 2019
 */
public class PlayerInputSystem extends AbstractSystem implements Window.MouseListener, Window.KeyListener {

    private final VoxelScene scene;

    private int selectedIdx = 0;

    public PlayerInputSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(Transformable.class, MovementComponent.class, WalkComponent.class, PlayerState.class, PlayerControls.class));
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

            float forwardSpeed = 0F, strafeSpeed = 0F, upwardSpeed = 0F;

            if (engine.getWindow().isKeyPressed(playerControls.getForward())) {
                forwardSpeed += 1F;
            }
            if (engine.getWindow().isKeyPressed(playerControls.getBackward())) {
                forwardSpeed += -1F;
            }

            if (engine.getWindow().isKeyPressed(playerControls.getLeft())) {
                strafeSpeed = 1F;
            }
            if (engine.getWindow().isKeyPressed(playerControls.getRight())) {
                strafeSpeed = -1F;
            }

            if (engine.getWindow().isKeyPressed(playerControls.getAscend())) {
                upwardSpeed = 1F;
            }
            if (engine.getWindow().isKeyPressed(playerControls.getDescend())) {
                upwardSpeed = -1F;
            }

            if (engine.getWindow().isKeyPressed(playerControls.getUpdateSun())) {
                scene.getSun().setDirection(scene.getCamera().getDirection());
            }

            forwardSpeed *= walkComponent.getAccelerationFactor();
            strafeSpeed  *= walkComponent.getAccelerationFactor();
            upwardSpeed  *= walkComponent.getAccelerationFactor();


            float dx = Mathf.sin(Mathf.toRadians(transformable.getRotation().y)) * forwardSpeed;
            float dz = Mathf.cos(Mathf.toRadians(transformable.getRotation().y)) * -1F * forwardSpeed;
            dx += Mathf.sin(Mathf.toRadians(transformable.getRotation().y - 90)) * strafeSpeed;
            dz += Mathf.cos(Mathf.toRadians(transformable.getRotation().y - 90)) * -1F * strafeSpeed;

            movementComponent.getAcceleration().x = dx;
            movementComponent.getAcceleration().y = upwardSpeed;
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

        selectedIdx += (yoffset > 0 ? 1 : -1);

        List<String> sortedVoxelIdentifiers = scene.getVoxelRegistry().getSortedVoxelIdentifiers();

        if (selectedIdx >= sortedVoxelIdentifiers.size()) {
            selectedIdx = 0;
        }
        if (selectedIdx < 0) {
            selectedIdx = sortedVoxelIdentifiers.size() - 1;
        }

        String voxelIdentifier = sortedVoxelIdentifiers.get(selectedIdx);

        scene.getPlayerState().setVoxelInHand(scene.getVoxelRegistry().getVoxel(voxelIdentifier));
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
