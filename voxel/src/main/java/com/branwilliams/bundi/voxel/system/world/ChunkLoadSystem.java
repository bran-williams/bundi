package com.branwilliams.bundi.voxel.system.world;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.components.CameraComponent;
import com.branwilliams.bundi.voxel.components.PlayerState;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 20, 2019
 */
public class ChunkLoadSystem extends AbstractSystem {

    private final VoxelScene scene;

    public ChunkLoadSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(Transformable.class, PlayerState.class, CameraComponent.class));
        this.scene = scene;
        scene.getEventManager().subscribe(PhysicsSystem.EntityMoveEvent.class, this::onEntityMove);
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        scene.getVoxelWorld().updateLightmap();
    }

    private void onEntityMove(PhysicsSystem.EntityMoveEvent event) {
        int maxDistance = scene.getGameSettings().getChunkRenderDistance();

        if (event.getEntity().hasComponent(CameraComponent.class)) {
            Transformable transformable = event.getEntity().getComponent(Transformable.class);
            Vector3f position = transformable.getPosition();
            scene.getVoxelWorld().loadChunks(position.x, position.z, maxDistance);
        }
    }
}
