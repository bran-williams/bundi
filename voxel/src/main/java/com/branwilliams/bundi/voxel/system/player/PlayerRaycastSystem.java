package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.voxel.math.RaycastResult;
import com.branwilliams.bundi.voxel.components.PlayerState;
import com.branwilliams.bundi.voxel.VoxelScene;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 05, 2019
 */
public class PlayerRaycastSystem extends AbstractSystem {

    private VoxelScene scene;

    public PlayerRaycastSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(PlayerState.class));
        this.scene = scene;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            PlayerState playerState = entity.getComponent(PlayerState.class);
            Vector3f position = scene.getCamera().getPosition();
            Vector3f direction = scene.getCamera().getFacingDirection();


            RaycastResult raycastResult = scene.getVoxelWorld().raycast(position, direction, playerState.getReachDistance());
            playerState.setRaycast(raycastResult);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }
}
