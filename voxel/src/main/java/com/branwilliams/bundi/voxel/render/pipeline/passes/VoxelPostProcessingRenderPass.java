package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.VoxelPostProcessingShaderProgram;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class VoxelPostProcessingRenderPass extends RenderPass<VoxelRenderContext> {

    private Supplier<Camera> camera;

    private VoxelPostProcessingShaderProgram postProcessingShaderProgram;

    public VoxelPostProcessingRenderPass(Supplier<Camera> camera) {
        this.camera = camera;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.postProcessingShaderProgram = new VoxelPostProcessingShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }

    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        this.postProcessingShaderProgram.bind();
        this.postProcessingShaderProgram.setProjection(renderContext.getProjection());
        this.postProcessingShaderProgram.setViewMatrix(camera.get());
        renderContext.bindTexturesForPostProcessing();
        MeshRenderer.render(renderContext.getRenderPassMesh(), null);
        renderContext.unbindTexturesForPostProcessing();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

}