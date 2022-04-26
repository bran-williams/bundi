package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class BloomViewRenderPass extends RenderPass<VoxelRenderContext> {

    private Supplier<Camera> camera;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    public BloomViewRenderPass(Supplier<Camera> camera) {
        this.camera = camera;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV);
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create post processing shader program!");
            throw new InitializationException(e);
        }

        this.dynamicVAO = new DynamicVAO(VertexFormat.POSITION_UV);
        this.dynamicVAO.begin();
        this.dynamicVAO.addRect(192 * 5, 0, 192 * 10, 108 * 5, 0, 1, 1, 0);
        this.dynamicVAO.compile();
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        this.shaderProgram.bind();
        this.shaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        this.shaderProgram.setModelMatrix(Transformable.empty());

        renderContext.getFinalBloomTexture().bind();
        dynamicVAO.draw();
        Texture.unbind(renderContext.getFinalBloomTexture());

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

}
