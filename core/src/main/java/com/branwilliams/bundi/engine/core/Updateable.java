package com.branwilliams.bundi.engine.core;

public interface Updateable {

    /**
     * Invoked from the {@link Engine} within the engine loop. This function is invoked as much as possible.
     *
     * @param engine The engine invoking this.
     * @param deltaTime The time (in seconds) passed between the previous update function.
     * */
    void update(Engine engine, double deltaTime);

    /**
     * Invoked from the {@link Engine} within the engine loop. This is invoked with a fixed rate, which can be set with
     * {@link Engine#setUpdateInterval(double)}.
     *
     * @param engine The engine playing this scene.
     * @param deltaTime The time (in seconds) passed between the previous update function.
     * */
    void fixedUpdate(Engine engine, double deltaTime);
}
