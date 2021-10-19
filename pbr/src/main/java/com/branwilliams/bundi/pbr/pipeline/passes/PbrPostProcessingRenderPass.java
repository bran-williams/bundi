package com.branwilliams.bundi.pbr.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.pbr.pipeline.shaders.PbrPostProcessingShaderProgram;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderContext;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class PbrPostProcessingRenderPass extends RenderPass<PbrRenderContext> {

    private final Supplier<Float> exposure;

    private PbrPostProcessingShaderProgram postProcessingShaderProgram;

    public PbrPostProcessingRenderPass(Supplier<Float> exposure) {
        this.exposure = exposure;
    }

    @Override
    public void init(PbrRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.postProcessingShaderProgram = new PbrPostProcessingShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }

    }

    @Override
    public void render(PbrRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        // draw to screen!!
        FrameBufferObject.unbind();

        glDepthMask(true);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.postProcessingShaderProgram.bind();
//        this.postProcessingShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.postProcessingShaderProgram.setExposure(exposure.get());

        renderContext.bindColorTexture();
        MeshRenderer.render(renderContext.getRenderPassMesh(), null);
        renderContext.unbindColorTexture();
    }

}
