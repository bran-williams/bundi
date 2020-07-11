package com.branwilliams.imageviewer.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.imageviewer.ImageViewerScene;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class ImageViewRenderPass extends RenderPass<RenderContext> {

    private ImageViewerScene scene;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    public ImageViewRenderPass(ImageViewerScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_2D_UV);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create shader program!", e);
        }

        dynamicVAO = new DynamicVAO(VertexFormat.POSITION_2D_UV);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (scene.hasGallery()) {
            shaderProgram.bind();
            shaderProgram.setProjectionMatrix(renderContext.getProjection());
            shaderProgram.setModelMatrix(scene.getImageViewParameters().getTransform());

            Texture texture = scene.getGallery().getSelectedTexture();

            texture.bind();
            float halfWidth = texture.getWidth() * 0.5F;
            float halfHeight = texture.getHeight() * 0.5F;

            drawRect(-halfWidth, -halfHeight, halfWidth, halfHeight, 0F, 0F, 1F, 1F);
        }
    }

    public void drawRect(float x, float y, float x1, float y1, float u, float v, float s, float t) {
        dynamicVAO.begin();

        dynamicVAO.position(x1, y).texture(s, v).endVertex();
        dynamicVAO.position(x, y).texture(u, v).endVertex();
        dynamicVAO.position(x, y1).texture(u, t).endVertex();
        dynamicVAO.position(x, y1).texture(u, t).endVertex();
        dynamicVAO.position(x1, y1).texture(s, t).endVertex();
        dynamicVAO.position(x1, y).texture(s, v).endVertex();

        dynamicVAO.draw();
    }
}
