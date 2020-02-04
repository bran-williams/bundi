package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since September 04, 2019
 */
public class CubesRenderer extends AbstractRenderer {

    public CubesRenderer(Scene scene, RenderPipeline renderPipeline) {
        super("cubesRenderer", scene, renderPipeline);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);
        glClearColor(0F, 0.75F, 1F, 1F);
        glEnable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(10F);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.render(engine, window, deltaTime);
    }
}
