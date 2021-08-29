package com.branwilliams.bundi.engine.ecs.matchers;

import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;

import java.util.function.Predicate;

/**
 * Created by Brandon Williams on 12/24/2018.
 */
public class PredicateComponentMatcher implements IComponentMatcher {

    private final Predicate<IEntity>[] predicates;

    @SafeVarargs
    public PredicateComponentMatcher(Predicate<IEntity>... predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean matches(IEntity entity) {
        for (Predicate<IEntity> predicate : predicates) {
            if (!predicate.test(entity)) {
                return false;
            }
        }
        return true;
    }
}
