package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainScene extends AbstractScene {

    private Projection worldProjection;

    public TerrainScene() {
        super("terrain_scene");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        worldProjection = new Projection(window, 70, 0.001F, 1000F);

        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        // renderPipeline.addLast(new TerrainRenderPass());
        setRenderer(new TerrainRenderer(this, renderPipeline));
    }

    @Override
    public void play(Engine engine) {

    }

    @Override
    public void pause(Engine engine) {

    }
}
