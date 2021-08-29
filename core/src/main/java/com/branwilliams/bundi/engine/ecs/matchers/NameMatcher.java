package com.branwilliams.bundi.engine.ecs.matchers;

import java.util.function.Supplier;

public class NameMatcher extends PredicateComponentMatcher {

    public NameMatcher(String name) {
        this(() -> name);
    }
    public NameMatcher(Supplier<String> name) {
        super((entity) -> name.get().equals(entity.getName()));
    }
}
