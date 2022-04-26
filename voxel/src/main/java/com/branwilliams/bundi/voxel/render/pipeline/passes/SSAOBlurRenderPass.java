package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.SSAOBlurShaderProgram;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class SSAOBlurRenderPass extends RenderPass<VoxelRenderContext> {


    private SSAOBlurShaderProgram ssaoBlurShaderProgram;

    public SSAOBlurRenderPass() {
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.ssaoBlurShaderProgram = new SSAOBlurShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {

        renderContext.getSSAOBlurFrameBuffer().bind();
        glClearColor(0F, 0F, 0F, 0.0F);
        glClear(GL_COLOR_BUFFER_BIT);

        this.ssaoBlurShaderProgram.bind();
        renderContext.bindTexturesForSSAOBlur();
        MeshRenderer.render(renderContext.getRenderPassMesh(), null);
        renderContext.unbindTexturesForSSAOBlur();
    }

}
