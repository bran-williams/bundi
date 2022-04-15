package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.voxel.components.MovementComponent;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
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
        super(new ClassComponentMatcher(Transformable.class, MovementComponent.class, PlayerState.class));
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
        if (this.getMatcher().matches(event.getEntity())) {
            // TODO update velocity/acceleration based on collision
            MovementComponent movementComponent = event.getEntity().getComponent(MovementComponent.class);
            PlayerState playerState = event.getEntity().getComponent(PlayerState.class);
            if (playerState.isNoClip()) {
                return;
            }

            AABB playerAABB = playerState.getBoundingBox();

            List<AABB> voxels = scene.getVoxelWorld().getVoxelsWithinAABB(playerAABB.expand(event.getMovement()),
                    (v) -> !v.isAir());

            Vector3f collidedMovement = event.getMovement();
            Vector3f originalMovement = new Vector3f(event.getMovement());


            // move in the y direction
            for (AABB aabb : voxels) {
                collidedMovement.y = aabb.clipYCollide(playerAABB, collidedMovement.y);
            }
            playerAABB.move(0.0F, collidedMovement.y, 0.0F);
            boolean hasCollidedWithGround = originalMovement.y != collidedMovement.y;


            // player touches the ground!
            if (collidedMovement.y < 0 && hasCollidedWithGround && !playerState.isOnGround()) {
//                System.out.println("Player touches the ground! originalY=" + originalMovement.y  + ", collidedY=" + collidedMovement.y);
                collidedMovement.y = 0F;
                playerState.setOnGround(true);
                movementComponent.getVelocity().y = 0;
                movementComponent.getAcceleration().y = 0;
            }

            if (originalMovement.y < 0 && collidedMovement.y == 0 && playerState.isOnGround()) {
                movementComponent.getVelocity().y = 0;
            }

//            else if (collidedMovement.y < 0 && playerState.isOnGround()) {
//                playerState.setOnGround(false);
//            }

            // move in the x direction
            for (AABB aabb : voxels) {
                collidedMovement.x = aabb.clipXCollide(playerAABB, collidedMovement.x);
            }
            playerAABB.move(collidedMovement.x, 0.0F, 0.0F);
            if (originalMovement.x != collidedMovement.x) {
                collidedMovement.x = 0F;
            }

            // move in the z direction
            for (AABB aabb : voxels) {
                collidedMovement.z = aabb.clipZCollide(playerAABB, collidedMovement.z);
            }
            playerAABB.move(0.0F, 0.0F, collidedMovement.z);
            if (originalMovement.z != collidedMovement.z) {
                collidedMovement.z = 0F;
            }
        }
    }
}
