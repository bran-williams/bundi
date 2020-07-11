package com.branwilliams.bundi.engine.core.pipeline;

import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;

/**
 * Holds the variables shared between render passes. This is held by the pipeline and it receives function calls
 * before every pass.
 * */
public class RenderContext implements Destructible {

    private Projection projection;

    public RenderContext(Projection projection) {
        this.projection = projection;
    }

    /**
     * Invoked before render passes are initialized. This is where any data that is needed for rendering should be
     * initialized such as a gbuffer, projections, meshes that are used throughout the render passes.
     * */
    public void init(Engine engine, Window window) {
        projection.update();
    }

    /**
     * Invoked whenever the window is resized. This occurs before the render passes. Any projections should be updated
     * and such.
     * */
    public void windowResized(Window window, int width, int height) {
        projection.update();
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    @Override
    public void destroy() {
    }
}
