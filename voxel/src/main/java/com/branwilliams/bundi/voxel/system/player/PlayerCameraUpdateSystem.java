package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.CameraComponent;
import com.branwilliams.bundi.voxel.components.PlayerState;

/**
 * Updates the camera rotation and position in order to follow an entities transformable and the mouse movements.
 *
 * @author Brandon
 * @since August 16, 2019
 */
public class PlayerCameraUpdateSystem extends AbstractSystem {

    public PlayerCameraUpdateSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(Transformable.class, PlayerState.class, CameraComponent.class));
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
            PlayerState playerState = entity.getComponent(PlayerState.class);
            cameraComponent.getCamera().setPosition(playerState.getEyePosition(transformable));
        }
    }
}
