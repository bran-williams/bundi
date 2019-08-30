package com.branwilliams.bundi.engine.selector;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class SelectorRenderPass extends RenderPass<RenderContext> {

    private static final int WHITE = 0xFFFFFFFF;

    private final SelectorScene selectorScene;

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    private Transformable worldTransform = new Transformation();

    private FontData normal = new FontData();

    private FontData bold = new FontData();

    public SelectorRenderPass(SelectorScene selectorScene) {
        this.selectorScene = selectorScene;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        fontRenderer = new BasicFontRenderer();
        normal.setFont(new Font("Default", Font.PLAIN, 20), true);
        bold.setFont(new Font("Default", Font.BOLD, 20), true);
        fontRenderer.setFontData(normal);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window) {
        glDisable(GL_DEPTH_TEST);
        worldTransform.setPosition(window.getWidth() * 0.5F, 20, 0F);

        this.shaderProgram.bind();
        this.shaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.shaderProgram.setModelMatrix(worldTransform);

        int y = 0;
        fontRenderer.drawCenteredString("Scenes Available:", 0, y, WHITE);
        y += fontRenderer.getFontData().getFontHeight();

        for (int i = 0; i < selectorScene.getScenes().size(); i++) {
            Class<? extends Scene> scene = selectorScene.getScenes().get(i);

            FontData font = i == selectorScene.getSelected() ? bold : normal;
            fontRenderer.drawCenteredString(font, scene.getSimpleName(), 0, y, WHITE);

            y += fontRenderer.getFontData().getFontHeight();
        }
        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        this.shaderProgram.destroy();
        this.fontRenderer.destroy();
        this.normal.destroy();
        this.bold.destroy();
    }
}
