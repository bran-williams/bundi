package com.branwilliams.imageviewer.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.imageviewer.ImageViewerScene;

import java.awt.*;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class ImageViewGuiRenderPass extends RenderPass<RenderContext> {

    private ImageViewerScene scene;

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    public ImageViewGuiRenderPass(ImageViewerScene scene) {
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
        fontRenderer.setFont(new Font("Arial", Font.BOLD, 18), true);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (scene.hasGallery()) {
            shaderProgram.bind();
            shaderProgram.setProjectionMatrix(renderContext.getProjection());
            shaderProgram.setModelMatrix(Transformable.empty());

            int y = 2;
            fontRenderer.drawString("Gallery: " + scene.getGallery().name(), 2, y, 0xFFFFFFFF);
            y += fontRenderer.getFontData().getFontHeight();

            fontRenderer.drawString(String.format("Image: (%d/%d)", scene.getGallery().getSelectedTextureIndex() + 1, scene.getGallery().getTextures().length), 2, y, 0xFFFFFFFF);
            y += fontRenderer.getFontData().getFontHeight();

        }
    }
}
