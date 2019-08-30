package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shape.Shape3f;

/**
 * Centers the Shape3f of an entity to the the entities transformation.
 * Created by Brandon Williams on 12/24/2018.
 */
public class Shape3fUpdateSystem extends AbstractSystem {

    private final CollidableUpdater updater;

    public Shape3fUpdateSystem() {
        this(((entity, transformable, shape3f) -> {
            shape3f.center(transformable.getPosition().x, transformable.getPosition().y, transformable.getPosition().z);
        }));
    }

    public Shape3fUpdateSystem(CollidableUpdater updater) {
        super(new ClassComponentMatcher(Transformable.class, Shape3f.class));
        this.updater = updater;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            Shape3f shape3F = entity.getComponent(Shape3f.class);
            updater.update(entity, transformable, shape3F);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    public interface CollidableUpdater {
        void update(IEntity entity, Transformable transformable, Shape3f shape3f);
    }
}
