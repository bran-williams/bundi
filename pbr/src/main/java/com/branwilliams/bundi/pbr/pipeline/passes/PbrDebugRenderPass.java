package com.branwilliams.bundi.pbr.pipeline.passes;

import com.branwilliams.bundi.engine.asset.FontLoader;
import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.pbr.PbrScene;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderContext;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderPipeline;

/**
 * @author Brandon
 * @since September 01, 2019
 */
public class PbrDebugRenderPass extends RenderPass<PbrRenderContext> {

    private final PbrScene scene;

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    private DynamicVAO dynamicVAO;

    public PbrDebugRenderPass(PbrScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(PbrRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create shader program!", e);
        }
        dynamicVAO = new DynamicVAO();

        fontRenderer = new BasicFontRenderer();
        FontLoader fontLoader = new FontLoader(engine.getContext());
        try {
            fontRenderer.setFontData(fontLoader.load("fonts/roboto/Roboto-Medium.ttf", 18));
        } catch (Exception e) {
            throw new InitializationException("Unable to load font!", e);
        }
    }

    @Override
    public void render(PbrRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        shaderProgram.setModelMatrix(Transformable.empty());

        fontRenderer.drawString("Press R to reload the material", 2, 2, 0xFFFFFFFF);
        int materialIndex = scene.getMaterialIndex();
        String[] materials = scene.getMaterials();
        fontRenderer.drawString(String.format("material=%s (%d/%d)", materials[materialIndex], materialIndex + 1,
                materials.length), 2, 2 + fontRenderer.getFontData().getFontHeight(), 0xFFFFFFFF);
        PbrRenderPipeline renderPipeline =
                (PbrRenderPipeline) ((AbstractRenderer<PbrRenderContext>) scene.getRenderer()).getRenderPipeline();

        int tinyWidth = 16 * 30;
        int tinyHeight = 9 * 30;
        renderPipeline.getRenderContext().getGBuffer().getAlbedo().bind();
        dynamicVAO.drawRect(2, 2, tinyWidth + 2, tinyHeight + 2,
                0, 0, 1, 1,
                1, 1, 1, 1);

        renderPipeline.getRenderContext().getGBuffer().getNormal().bind();
        dynamicVAO.drawRect(tinyWidth + 4, 2, tinyWidth * 2 + 6, tinyHeight + 2,
                0, 0, 1, 1,
                1, 1, 1, 1);
        ShaderProgram.unbind();
    }
}
