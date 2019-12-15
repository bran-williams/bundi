package com.branwilliams.bundi.voxel.system.world;

import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.tukio.Event;
import com.branwilliams.bundi.voxel.components.MovementComponent;
import org.joml.Vector3f;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class PhysicsSystem extends AbstractSystem {

    private static final float MINIMUM_VELOCITY = 0.0001F;

    private Scene scene;

    private Vector3f gravity;

    private Vector3f velocityDamper;

    public PhysicsSystem(Scene scene) {
        this(scene, new Vector3f());
    }

    public PhysicsSystem(Scene scene, Vector3f gravity) {
        this(scene, gravity, new Vector3f(0.9F));
    }

    public PhysicsSystem(Scene scene, Vector3f gravity, Vector3f velocityDamper) {
        super(new ClassComponentMatcher(Transformable.class, MovementComponent.class));
        this.scene = scene;
        this.gravity  = gravity;
        this.velocityDamper = velocityDamper;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {}

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            MovementComponent movementComponent = entity.getComponent(MovementComponent.class);

            float moveX = (float) (movementComponent.getVelocity().x * movementComponent.getMovementSpeed() * deltaTime);
            float moveY = (float) (movementComponent.getVelocity().y * movementComponent.getMovementSpeed() * deltaTime);
            float moveZ = (float) (movementComponent.getVelocity().z * movementComponent.getMovementSpeed() * deltaTime);
            Vector3f movement = new Vector3f(moveX, moveY, moveZ);
//            System.out.println("moveX=" + moveX + ", moveY=" + moveY + ", moveZ=" + moveZ + "");
//            System.out.println("gravityX=" + (float) (gravity.x * deltaTime) + ", gravityY=" + (float) (gravity.y * deltaTime) + ", gravityZ=" + (float) (gravity.z * deltaTime));

            // Add a little splash of gravity
            movement.add((float) (gravity.x * deltaTime), (float) (gravity.y * deltaTime), (float) (gravity.z * deltaTime));

            if (Mathf.abs(movement.lengthSquared()) >= 0F) {
                EntityMoveEvent moveEvent = new EntityMoveEvent(entity, transformable, movementComponent, movement);
                scene.getEventManager().publish(moveEvent);
            }

            transformable.getPosition().x += movement.x;
            transformable.getPosition().y += movement.y;
            transformable.getPosition().z += movement.z;

            movementComponent.getVelocity().x += movementComponent.getAcceleration().x * deltaTime;
            movementComponent.getVelocity().y += movementComponent.getAcceleration().y * deltaTime;
            movementComponent.getVelocity().z += movementComponent.getAcceleration().z * deltaTime;

            movementComponent.getVelocity().mul(velocityDamper);

            if (Math.abs(movementComponent.getVelocity().x) < MINIMUM_VELOCITY) {
                movementComponent.getVelocity().x = 0F;
            }

            if (Math.abs(movementComponent.getVelocity().y) < MINIMUM_VELOCITY) {
                movementComponent.getVelocity().y = 0F;
            }

            if (Math.abs(movementComponent.getVelocity().z) < MINIMUM_VELOCITY) {
                movementComponent.getVelocity().z = 0F;
            }
        }
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    public Vector3f getVelocityDamper() {
        return velocityDamper;
    }

    public void setVelocityDamper(Vector3f velocityDamper) {
        this.velocityDamper = velocityDamper;
    }

    public static class EntityMoveEvent implements Event {

        private final IEntity entity;

        private final Transformable transformable;

        private final MovementComponent movementComponent;

        private Vector3f movement;

        public EntityMoveEvent(IEntity entity, Transformable transformable, MovementComponent movementComponent, Vector3f movement) {
            this.entity = entity;
            this.transformable = transformable;
            this.movementComponent = movementComponent;
            this.movement = movement;
        }

        public IEntity getEntity() {
            return entity;
        }

        public Transformable getTransformable() {
            return transformable;
        }

        public MovementComponent getMovementComponent() {
            return movementComponent;
        }

        public Vector3f getMovement() {
            return movement;
        }

        public void setMovement(Vector3f movement) {
            this.movement = movement;
        }
    }
}