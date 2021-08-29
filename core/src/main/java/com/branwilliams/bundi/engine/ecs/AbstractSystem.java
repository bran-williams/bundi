package com.branwilliams.bundi.engine.ecs;

/**
 * Basic implementation of {@link ISystem}.
 * Created by Brandon Williams on 6/24/2018.
 */
public abstract class AbstractSystem implements ISystem {

    private EntitySystemManager es;

    private final IComponentMatcher matcher;

    public AbstractSystem(IComponentMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public IComponentMatcher getMatcher() {
        return matcher;
    }

    @Override
    public void setEs(EntitySystemManager es) {
        this.es = es;
    }

    @Override
    public EntitySystemManager getEs() {
        return es;
    }

    @Override
    public void destroy() {
    }
}
