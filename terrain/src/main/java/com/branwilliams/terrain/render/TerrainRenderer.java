package com.branwilliams.terrain.render;

import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainRenderer extends AbstractRenderer {

    public TerrainRenderer(Scene scene, RenderPipeline renderPipeline) {
        super("TerrainRenderer", scene, renderPipeline);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);
        glClearColor(0.1F, 0.5F, 1F, 1F);
        glEnable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.render(engine, window, deltaTime);
    }
}
