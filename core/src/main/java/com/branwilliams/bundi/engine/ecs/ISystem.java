package com.branwilliams.bundi.engine.ecs;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;

/**
 * A System performs any logic on a list of entities who have the components that satisfy the systems component matcher.
 * Created by Brandon Williams on 6/24/2018.
 */
public interface ISystem {

    /**
     * Initialized by the {@link EntitySystemManager}.
     * */
    void init(Engine engine, EntitySystemManager entitySystemManager, Window window);

    /**
     * @return A {@link IComponentMatcher} which this system requires.
     * */
    IComponentMatcher getMatcher();

    /**
     * Invoked from the engine before rendering. This is invoked as often as the render pass, regardless of the time it
     * took.
     * @param deltaTime The time (in ms) passed between the previous update function.
     * */
    void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime);

    /**
     * Invoked from the engine before rendering. This is invoked with a fixed rate, which can be set with
     * {@link Engine#setUpdateInterval(double)}.
     * @param deltaTime The time (in ms) passed between the previous update function.
     * */
    void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime);

    /**
     * Assigns this systems {@link EntitySystemManager}. This function is only meant to be used by this systems manager.
     * */
    void setEs(EntitySystemManager es);

    /**
     * Getter for this systems {@link EntitySystemManager}.
     * */
    EntitySystemManager getEs();
}
