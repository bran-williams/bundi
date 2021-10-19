package com.branwilliams.bundi.pbr;

import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since August 31, 2019
 */
public class PbrRenderer extends AbstractRenderer {

    public PbrRenderer(Scene scene, RenderPipeline renderPipeline) {
        super("PbrRenderer", scene, renderPipeline);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);
        glClearColor(0F, 0F, 0F, 1.0F);
        glEnable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.render(engine, window, deltaTime);
    }
}
