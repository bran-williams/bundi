package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.framebuffers.BloomPingPongFrameBuffer;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.BloomBlurShaderProgram;
import com.branwilliams.bundi.voxel.scene.VoxelScene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class BloomBlurRenderPass extends RenderPass<VoxelRenderContext> {

    private static final int BLUR_PASSES = 10;

    private BloomBlurShaderProgram bloomBlurShaderProgram;

    public BloomBlurRenderPass(VoxelScene scene) {}

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.bloomBlurShaderProgram = new BloomBlurShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create bloom blur shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        glActiveTexture(GL_TEXTURE0);

//        renderContext.getBloomPingFrameBuffer().bind();
//        glClearColor(0F, 0F, 0F, 0.0F);
//        glClear(GL_COLOR_BUFFER_BIT);
//        renderContext.getBloomPongFrameBuffer().bind();
//        glClearColor(0F, 0F, 0F, 0.0F);
//        glClear(GL_COLOR_BUFFER_BIT);

        this.bloomBlurShaderProgram.bind();

        boolean horizontal = true, firstIteration = true;
        BloomPingPongFrameBuffer[] frameBuffers = { renderContext.getBloomPingFrameBuffer(),
                renderContext.getBloomPongFrameBuffer() };

        for (int i = 0; i < BLUR_PASSES; i++) {

            frameBuffers[horizontal ? 1 : 0].bind();

            this.bloomBlurShaderProgram.setHorizontal(horizontal);

            if (firstIteration) {
                firstIteration = false;
                renderContext.getGBuffer().getEmission().bind();
            } else {
                frameBuffers[horizontal ? 0 : 1].getTexture().bind();
            }
            MeshRenderer.render(renderContext.getRenderPassMesh(), null);

            horizontal = !horizontal;
        }
        renderContext.setFinalBloomTexture(frameBuffers[horizontal ? 0 : 1].getTexture());
    }

}
