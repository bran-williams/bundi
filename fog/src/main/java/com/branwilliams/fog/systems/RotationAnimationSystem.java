package com.branwilliams.fog.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.fog.RotationAnimation;

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

            transformable.getRotation().x = rotationAnimation.getAxis().x;
            transformable.getRotation().y = rotationAnimation.getAxis().y;
            transformable.getRotation().z = rotationAnimation.getAxis().z;
            transformable.getRotation().w += rotationAnimation.getRotationSpeed() * deltaTime;
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }
}
