package com.branwilliams.bundi.engine.core;

/**
 * Performs the rendering stage of the engine's update loop. <br/>
 * The {@link Renderer#render(Engine, Window, double)} function is invoked every loop after the update functions of the current
 * scene.
 *
 * Created by Brandon on 9/5/2016.
 */
public interface Renderer extends Nameable, Destructible {

    /**
     * Initializes this renderer. This function is invoked once after initializing the scene. If the scene is set to be
     * destroyed upon replacement, it will be invoked every time the scene is set within the engine.
     * @param engine The engine running this renderer.
     * @param window The window this renderer is rendering onto.
     * */
    void init(Engine engine, Window window) throws Exception;

    /**
     * Invoked every frame to render onto a {@link Window}.
     * @param engine The engine running this renderer.
     * @param window The window this renderer is rendering onto.
     * @param deltaTime The amount of time (in seconds) between the last frame and this frame.
     * */
    void render(Engine engine, Window window, double deltaTime);

}
