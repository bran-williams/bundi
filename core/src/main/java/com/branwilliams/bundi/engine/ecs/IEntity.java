package com.branwilliams.bundi.engine.ecs;

import com.branwilliams.bundi.engine.core.Nameable;

import java.util.Map;

/**
 * An Entity simply contains an id and a list of components.
 * Created by Brandon Williams on 6/24/2018.
 */
public interface IEntity extends Nameable {

    /**
     * @return The unique id for this entity instance
     * */
    Integer getId();

    /**
     * @return A map of components this entity contains.
     * */
    Map<Class<?>, Object> getComponents();

    /**
     * @return The component with the associated class.
     * */
    <T> T getComponent(Class<? extends T> clazz);

    /**
     * @return True if this entity has a component with the associated class.
     * */
    boolean hasComponent(Class<?> clazz);

    /**
     * Adds a component to this entity.
     * */
    default void addComponent(Object component) {
        getComponents().put(component.getClass(), component);
    }

    /**
     * Removes a component from this entity.
     * @return True if the component existed.
     * */
    default boolean removeComponent(Object component) {
        return getComponents().remove(component.getClass()) != null;
    }
}
