package com.branwilliams.fog.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.fog.ParticleEmitter;

public class ParticleUpdateSystem extends AbstractSystem {

    public ParticleUpdateSystem() {
        super(new ClassComponentMatcher(Transformable.class, ParticleEmitter.class));
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
            ParticleEmitter particleEmitter = entity.getComponent(ParticleEmitter.class);
            particleEmitter.getSpawnPosition().set(entity.getComponent(Transformable.class).getPosition());
            particleEmitter.updateParticles(deltaTime);
        }
    }
}
