package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.systems.MouseControlSystem;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.CameraComponent;
import com.branwilliams.bundi.voxel.components.PlayerState;

/**
 * Updates the camera rotation and position in order to follow an entities transformable and the mouse movements.
 *
 * @author Brandon
 * @since August 16, 2019
 */
public class PlayerCameraUpdateSystem extends MouseControlSystem {

    public PlayerCameraUpdateSystem(VoxelScene scene) {
        super(scene, scene, new ClassComponentMatcher(Transformable.class, PlayerState.class, CameraComponent.class));
    }

    @Override
    protected void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval, float mouseX, float mouseY, float oldMouseX, float oldMouseY) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);

            Camera camera = cameraComponent.getCamera();
            camera.rotate((mouseY - oldMouseY) * cameraComponent.getCameraSpeed(), (mouseX - oldMouseX) * cameraComponent.getCameraSpeed(), 0F);
            transformable.setRotation(camera.getRotation());
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        super.fixedUpdate(engine, entitySystemManager, deltaTime);
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            CameraComponent cameraComponent = entity.getComponent(CameraComponent.class);
            PlayerState playerState = entity.getComponent(PlayerState.class);
            cameraComponent.getCamera().setPosition(playerState.getEyePosition(transformable));
        }
    }
}
