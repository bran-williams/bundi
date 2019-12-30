package com.branwilliams.imageviewer.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.imageviewer.Gallery;
import com.branwilliams.imageviewer.GallerySelectorScene;

import java.awt.*;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class GallerySelectorRenderPass extends RenderPass<RenderContext> {

    private GallerySelectorScene scene;

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    private FontData normal;

    private FontData bold;

    public GallerySelectorRenderPass(GallerySelectorScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create shader program!", e);
        }

        fontRenderer = new BasicFontRenderer();
        fontRenderer.setFont(new Font("Arial", Font.PLAIN, 18), true);
        bold = new FontData().setFont(new Font("Arial", Font.BOLD, 18), true);
        normal = fontRenderer.getFontData();
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setModelMatrix(Transformable.empty());
        int y = 2;

        for (int i = 0; i < scene.getGalleries().length; i++) {
            Gallery gallery = scene.getGalleries()[i];
            FontData fontData = i == scene.getSelectedGallery() ? bold : normal;
            String text = "[" + i + "] " + gallery.name() + ": " + gallery.files.length;
            fontRenderer.drawString(fontData, text, 2, y, 0xFFFFFFFF);
            y += fontData.getFontHeight();
        }
    }
}
