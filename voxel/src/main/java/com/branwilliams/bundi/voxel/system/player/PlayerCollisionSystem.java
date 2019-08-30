package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.PlayerState;
import com.branwilliams.bundi.voxel.system.world.PhysicsSystem;
import org.joml.Vector3f;

import java.util.List;

/**
 * @author Brandon
 * @since August 12, 2019
 */
public class PlayerCollisionSystem extends AbstractSystem {

    private final VoxelScene scene;

    public PlayerCollisionSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(Transformable.class, PlayerState.class));
        this.scene = scene;
        this.scene.getEventManager().subscribe(PhysicsSystem.EntityMoveEvent.class, this::onEntityMove);
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
            PlayerState playerState = entity.getComponent(PlayerState.class);
            playerState.updateBoundingBox(transformable);
        }
    }

    protected void onEntityMove(PhysicsSystem.EntityMoveEvent event) {
        if (event.getEntity().hasComponent(PlayerState.class)) {
            PlayerState playerState = event.getEntity().getComponent(PlayerState.class);
            if (playerState.isNoClip()) {
                return;
            }

            AABB playerAABB = playerState.getBoundingBox();

            List<AABB> voxels = scene.getVoxelWorld().queryVoxelsWithinAABB(playerAABB.expand(event.getMovement()),
                    (v) -> !v.isAir());

            Vector3f movement = event.getMovement();
            Vector3f originalMovement = new Vector3f(event.getMovement());

            for (AABB aabb : voxels)
                movement.y = aabb.clipYCollide(playerAABB, movement.y);
            playerAABB.move(0.0F, movement.y, 0.0F);

            for (AABB aabb : voxels)
                movement.x = aabb.clipXCollide(playerAABB, movement.x);
            playerAABB.move(movement.x, 0.0F, 0.0F);

            for (AABB aabb : voxels)
                movement.z = aabb.clipZCollide(playerAABB, movement.z);
            playerAABB.move(0.0F, 0.0F, movement.z);

            boolean onGround = ((movement.y != originalMovement.y) && (originalMovement.y < 0.0F));
            playerState.setOnGround(onGround);

            if (originalMovement.x != movement.x) {
                event.getMovementComponent().getVelocity().x = 0F;
                event.getMovementComponent().getAcceleration().x = 0F;
            }

            if (originalMovement.y != movement.y) {
                event.getMovementComponent().getVelocity().y = 0F;
                event.getMovementComponent().getAcceleration().y = 0F;
            }

            if (originalMovement.z != movement.z) {
                event.getMovementComponent().getVelocity().z = 0F;
                event.getMovementComponent().getAcceleration().z = 0F;
            }

//            movement.x = 0F;
//            movement.y = 0F;
//            movement.z = 0F;
        }
    }
}
