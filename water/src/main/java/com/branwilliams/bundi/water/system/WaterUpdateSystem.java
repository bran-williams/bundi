package com.branwilliams.bundi.water.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.water.Water;

/**
 * @author Brandon
 * @since September 04, 2019
 */
public class WaterUpdateSystem extends AbstractSystem {

    private static final float TIME_PER_UPDATE = 0.2F;

    public WaterUpdateSystem() {
        super(new ClassComponentMatcher(Water.class));
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
            Water water = entity.getComponent(Water.class);
            water.setPassedTime(water.getPassedTime() + (float) (TIME_PER_UPDATE * deltaTime));
        }
    }
}
