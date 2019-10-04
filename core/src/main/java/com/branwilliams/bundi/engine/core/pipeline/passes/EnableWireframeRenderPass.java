package com.branwilliams.bundi.engine.core.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since June 14, 2019
 */
public class EnableWireframeRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Boolean> wireframe;

    public EnableWireframeRenderPass(Supplier<Boolean> wireframe) {
        this.wireframe = wireframe;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {

    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (wireframe.get())
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
}
