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
import com.branwilliams.bundi.voxel.render.pipeline.shaders.SSAOShaderProgram;
import com.branwilliams.bundi.voxel.scene.VoxelScene;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class SSAORenderPass extends RenderPass<VoxelRenderContext> {

    private final Supplier<Float> aoPower;

    private SSAOShaderProgram ssaoShaderProgram;

    public SSAORenderPass(VoxelScene scene) {
        this.aoPower = () -> scene.getGameSettings().getAmbientOcclusionPower();
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.ssaoShaderProgram = new SSAOShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }

    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        renderContext.getSSAOFrameBuffer().bind();
        glClearColor(0F, 0F, 0F, 0.0F);
        glClear(GL_COLOR_BUFFER_BIT);

        this.ssaoShaderProgram.bind();
        this.ssaoShaderProgram.setProjection(renderContext.getProjection());
        this.ssaoShaderProgram.setScreenSize(window.getWidth(), window.getHeight());
        this.ssaoShaderProgram.setSamples(renderContext.getSSAOKernel());
        this.ssaoShaderProgram.setPower(aoPower.get());
        this.ssaoShaderProgram.setRadius(0.2F);
        this.ssaoShaderProgram.setBias(0.03F);

        renderContext.bindTexturesForSSAO();
        MeshRenderer.render(renderContext.getRenderPassMesh(), null);
        renderContext.unbindTexturesForSSAO();

    }

}
