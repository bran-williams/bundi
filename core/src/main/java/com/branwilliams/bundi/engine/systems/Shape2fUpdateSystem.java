package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.shape.Shape2f;

/**
 * Centers the Shape2f of an entity to the the entities transformation.
 * Created by Brandon Williams on 12/24/2018.
 */
public class Shape2fUpdateSystem extends AbstractSystem {

    private final ShapeUpdater updater;

    public Shape2fUpdateSystem() {
        this(((entity, transformable, shape2f) -> {
            shape2f.center(transformable.getPosition().x, transformable.getPosition().y);
        }));
    }

    public Shape2fUpdateSystem(ShapeUpdater updater) {
        super(new ClassComponentMatcher(Transformable.class, Shape2f.class));
        this.updater = updater;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            Shape2f shape2F = entity.getComponent(Shape2f.class);
            updater.update(entity, transformable, shape2F);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    public interface ShapeUpdater {
        void update(IEntity entity, Transformable transformable, Shape2f shape2F);
    }
}
