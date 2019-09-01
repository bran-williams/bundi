package com.branwilliams.terrain.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.systems.MouseControlSystem;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class RotatingCameraSystem extends MouseControlSystem {

    private static final float MOUSE_MOVE_AMOUNT = 0.16F;

    private final Transformable focalPoint;

    private final Vector3f radius;

    private float yaw = 0;

    public RotatingCameraSystem(Scene scene, Transformable focalPoint, Vector3f radius) {
        super(scene, new ClassComponentMatcher(Camera.class));
        this.focalPoint = focalPoint;
        this.radius = radius;
    }

    @Override
    protected void mouseMove(Engine engine, EntitySystemManager entitySystemManager, double interval, float mouseX,
                             float mouseY, float oldMouseX, float oldMouseY) {
        float moveX = (mouseX - oldMouseX) * MOUSE_MOVE_AMOUNT;
        yaw += moveX;

        if (yaw > 360)
            yaw = yaw % 360;
        else if (yaw < 0)
            yaw = 360 + yaw;


    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        super.fixedUpdate(engine, entitySystemManager, deltaTime);
        float yawRadians = 2F * Mathf.PI * (yaw / 360F);
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Camera camera = entity.getComponent(Camera.class);
            camera.setPosition(radius.x + Mathf.cos(yawRadians) * radius.x, radius.y, radius.z + Mathf.sin(yawRadians) * radius.z);
            camera.lookAt(focalPoint.getPosition());
        }
    }
}
