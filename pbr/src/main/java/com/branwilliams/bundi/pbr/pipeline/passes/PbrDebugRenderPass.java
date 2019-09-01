package com.branwilliams.bundi.pbr.pipeline.passes;

import com.branwilliams.bundi.engine.asset.FontLoader;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderContext;

/**
 * @author Brandon
 * @since September 01, 2019
 */
public class PbrDebugRenderPass extends RenderPass<PbrRenderContext> {

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    @Override
    public void init(PbrRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create shader program!", e);
        }

        fontRenderer = new BasicFontRenderer();
        FontLoader fontLoader = new FontLoader(engine.getContext());
        try {
            fontRenderer.setFontData(fontLoader.load("fonts/roboto/Roboto-Medium.ttf", 18));
        } catch (Exception e) {
            throw new InitializationException("Unable to load font!", e);
        }
    }

    @Override
    public void render(PbrRenderContext renderContext, Engine engine, Window window) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        shaderProgram.setModelMatrix(Transformable.empty());

        fontRenderer.drawString("Press R to reload the material", 2, 2, 0xFFFFFFFF);

        ShaderProgram.unbind();
    }
}
