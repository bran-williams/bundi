package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McSkinRenderer extends AbstractRenderer {

    public McSkinRenderer(Scene scene, RenderPipeline renderPipeline) {
        super("mcskinrenderer", scene, renderPipeline);
    }


    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);
        glClearColor(0.3F, 0.5F, 0.8F, 1F);
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
