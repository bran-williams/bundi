package com.branwilliams.bundi.gui.demo;

import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since September 04, 2019
 */
public class GuiDemoRenderer<Context extends RenderContext> extends AbstractRenderer<Context> {

    private final Vector4f clearColor = new Vector4f(0F, 0.75F, 1F, 1F);

    public GuiDemoRenderer(Scene scene, RenderPipeline<Context> renderPipeline) {
        super("templateRenderer", scene, renderPipeline);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);

        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
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
