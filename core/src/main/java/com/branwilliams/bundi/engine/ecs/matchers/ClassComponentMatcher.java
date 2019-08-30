package com.branwilliams.bundi.engine.ecs.matchers;

import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;

import java.util.Arrays;

/**
 * Basic implementation of {@link IComponentMatcher} which matches for entities with components matching some classes.
 * Created by Brandon Williams on 6/24/2018.
 */
public class ClassComponentMatcher implements IComponentMatcher {

    private final Class<?>[] components;

    public ClassComponentMatcher(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            throw new IllegalArgumentException("At least one component must be specified!");
        }

        this.components = classes;
    }

    @Override
    public boolean matches(IEntity entity) {
        for (Class<?> component : components) {
            if (!entity.hasComponent(component)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassComponentMatcher)) return false;
        ClassComponentMatcher that = (ClassComponentMatcher) o;
        return Arrays.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }

    @Override
    public String toString() {
        return "ClassComponentMatcher{" +
                "components=" + Arrays.toString(components) +
                '}';
    }
}
