package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;

/**
 * Created by Brandon Williams on 9/15/2019.
 */
public class AtmosphereScene extends AbstractScene {

    private Camera camera;

    public AtmosphereScene() {
        super("atmosphere");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);

        AtmosphereRenderer renderer = new AtmosphereRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.lookAt(0, 0, -2);
    }

    @Override
    public void pause(Engine engine) {

    }

    public Camera getCamera() {
        return camera;
    }
}
