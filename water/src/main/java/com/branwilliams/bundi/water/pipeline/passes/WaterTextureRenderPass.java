package com.branwilliams.bundi.water.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.water.Water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Brandon
 * @since September 04, 2019
 */
public class WaterTextureRenderPass extends RenderPass<RenderContext> {

    private final Water water;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    private Projection orthoProjection;

    public WaterTextureRenderPass(Water water) {
        this.water = water;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        dynamicVAO = new DynamicVAO();

        orthoProjection = new Projection(window);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {

        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(orthoProjection);
        shaderProgram.setModelMatrix(Transformable.empty());

        glActiveTexture(GL_TEXTURE0);
        water.getNormalBuffer().getNormal().bind();
        dynamicVAO.begin();
        dynamicVAO.addRect(0, 0, 1024, 1024,
                0F, 0F, 1F, 1F,
                1F, 1F, 1F, 1F);
        dynamicVAO.draw();
        Texture.unbind(water.getNormalBuffer().getNormal());

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
        this.dynamicVAO.destroy();
    }
}
