package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.NameMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.frogger.components.FrogmanMovementComponent;

import static com.branwilliams.frogger.FroggerConstants.FROGMAN_NAME;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

public class FrogmanMovementSystem extends AbstractSystem {

    public FrogmanMovementSystem() {
        super(new NameMatcher(FROGMAN_NAME));
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        float keyMoveSpeed = 200F;
        for (IEntity frogman : entitySystemManager.getEntities(this)) {
            Transformable frogmanTransform = frogman.getComponent(Transformable.class);
            FrogmanMovementComponent movementComponent = frogman.getComponent(FrogmanMovementComponent.class);

            if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT)) {
                frogmanTransform.move(keyMoveSpeed * (float) deltaTime, 0, 0);
            }

            if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT)) {
                frogmanTransform.move(-keyMoveSpeed * (float) deltaTime, 0, 0);
            }

            boolean doGravity = true;
            if (engine.getWindow().isKeyPressed(GLFW_KEY_UP)) {
                frogmanTransform.move(0, -keyMoveSpeed * (float) deltaTime, 0);
                doGravity = false;
            }

            if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN)) {
                frogmanTransform.move(0, keyMoveSpeed * (float) deltaTime, 0);
                doGravity = false;
            }

            if (doGravity) {
                frogmanTransform.move(0, 90 * (float) deltaTime, 0);
            }

            AABB2f frogmanAABB = frogman.getComponent(AABB2f.class);

            frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());
        }

    }
}
