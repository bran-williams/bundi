package com.branwilliams.bundi.engine.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds basic entities for an {@link EntitySystemManager}.
 * Created by Brandon Williams on 6/24/2018.
 */
public class EntityBuilder {

    private EntitySystemManager entitySystemManager;

    private Integer id;

    private String name;

    private Map<Class<?>, Object> components = new HashMap<>();

    // This is used to indicate whether or not this builder has already built.
    private boolean finished = false;

    EntityBuilder(EntitySystemManager entitySystemManager, Integer id) {
        this.entitySystemManager = entitySystemManager;
        this.id = id;
    }

    /**
     * Assigns a name for the entity this builder is creating.
     * */
    public EntityBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The provided components will be assigned to this builder's entity.
     * */
    public EntityBuilder component(Object... components) {
        for (Object component : components)
            if (component != null)
                this.components.put(component.getClass(), component);
        return this;
    }

    /**
     * @return The {@link IEntity} this builder has built. Null if it has already been built.
     * @throws IllegalArgumentException When the entity has not been assigned a name or if that name is empty.
     * */
    public IEntity build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The name for this entity cannot be empty!");
        }

        if (finished) {
            return null;
        }

        IEntity entity = new BasicEntity(entitySystemManager, id, name);
        entity.getComponents().putAll(components);

        entitySystemManager.addEntity(entity);
        finished = true;
        return entity;
    }
}