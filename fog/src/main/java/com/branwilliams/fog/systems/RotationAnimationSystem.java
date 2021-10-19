package com.branwilliams.fog.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.fog.AxisAngle;
import com.branwilliams.fog.RotationAnimation;
import org.joml.AxisAngle4f;

public class RotationAnimationSystem extends AbstractSystem {

    public RotationAnimationSystem() {
        super(new ClassComponentMatcher(Transformable.class, RotationAnimation.class));
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            RotationAnimation rotationAnimation = entity.getComponent(RotationAnimation.class);
            AxisAngle4f axisAngle = rotationAnimation.getAxisAngle();
            axisAngle.rotate((float) (rotationAnimation.getRotationSpeed() * deltaTime));
            transformable.getRotation().set(axisAngle);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }
}
