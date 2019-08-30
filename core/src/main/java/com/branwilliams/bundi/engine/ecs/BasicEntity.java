package com.branwilliams.bundi.engine.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of {@link IEntity}. This can only be created by an {@link EntitySystemManager} via the
 * {@link EntityBuilder}.
 * Created by Brandon Williams on 6/24/2018.
 */
public final class BasicEntity implements IEntity {

    private final EntitySystemManager manager;

    private final int id;

    private final String name;

    private final Map<Class<?>, Object> components = new HashMap<>();

    BasicEntity(EntitySystemManager manager, int id, String name) {
        this.manager = manager;
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Map<Class<?>, Object> getComponents() {
        return components;
    }

    @Override
    public <T> T getComponent(Class<? extends T> clazz) {
        for (Class<?> componentType : components.keySet()) {
            if (clazz.isAssignableFrom(componentType)) {
                return (T) components.get(componentType);
            }
        }
        return null;
        //return (T) components.get(clazz);
    }

    @Override
    public boolean hasComponent(Class<?> clazz) {
        return components.keySet().stream()
                .anyMatch(clazz::isAssignableFrom);
        //return components.containsKey(clazz);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addComponent(Object component) {
        Object old = getComponents().put(component.getClass(), component);
        manager.updateEntity(this);
    }

    @Override
    public boolean removeComponent(Object component) {
        boolean removed = getComponents().remove(component.getClass()) != null;
        if (removed) {
            manager.updateEntity(this);
        }
        return removed;
    }

    @Override
    public String toString() {
        return "BasicEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", components=" + components.values() +
                '}';
    }
}
