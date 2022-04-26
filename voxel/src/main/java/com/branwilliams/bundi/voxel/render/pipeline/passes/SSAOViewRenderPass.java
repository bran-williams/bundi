package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.SSAOViewShaderProgram;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class SSAOViewRenderPass extends RenderPass<VoxelRenderContext> {

    private Supplier<Camera> camera;

    private SSAOViewShaderProgram ssaoViewShaderProgram;

    private DynamicVAO dynamicVAO;

    public SSAOViewRenderPass(Supplier<Camera> camera) {
        this.camera = camera;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.ssaoViewShaderProgram = new SSAOViewShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }

        this.dynamicVAO = new DynamicVAO(VertexFormat.POSITION_UV);
        this.dynamicVAO.begin();
        this.dynamicVAO.addRect(0, 0, 192 * 5, 108 * 5, 0, 1, 1, 0);
        this.dynamicVAO.compile();
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        this.ssaoViewShaderProgram.bind();
        this.ssaoViewShaderProgram.setProjection(renderContext.getOrthoProjection());
        this.ssaoViewShaderProgram.setViewMatrix(camera.get());

        renderContext.bindTexturesForPostProcessing();
        dynamicVAO.draw();
        renderContext.unbindTexturesForPostProcessing();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

}
