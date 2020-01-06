package com.branwilliams.bundi.voxel.system.world;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.components.Atmosphere;
import org.joml.Vector3f;

public class AtmosphereSystem extends AbstractSystem {

    private float sunYaw;

    private float sunPitch = -90;

    public AtmosphereSystem() {
        super(new ClassComponentMatcher(Atmosphere.class));
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Vector3f direction = new Vector3f();

        sunPitch = -90 + (float) engine.getTime();
        float yaw   = Mathf.toRadians(sunYaw);
        float pitch = Mathf.toRadians(sunPitch);

        direction.x = Mathf.cos(pitch) * Mathf.sin(yaw);
        direction.y = -Mathf.sin(pitch);
        direction.z = -Mathf.cos(pitch) * Mathf.cos(yaw);

        Vector3f sunPosition = direction.normalize().negate();

        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Atmosphere atmosphere = entity.getComponent(Atmosphere.class);
            atmosphere.getSun().setDirection(sunPosition);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
    }
}
