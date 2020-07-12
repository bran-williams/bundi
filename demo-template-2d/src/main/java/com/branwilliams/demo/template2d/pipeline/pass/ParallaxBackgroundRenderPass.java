package com.branwilliams.demo.template2d.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.demo.template2d.parallax.ParallaxBackground;
import com.branwilliams.demo.template2d.parallax.ParallaxLayer;
import com.branwilliams.demo.template2d.parallax.ParallaxObject;

import java.util.function.Supplier;

public class ParallaxBackgroundRenderPass<CurrentContext extends RenderContext> extends RenderPass<CurrentContext> {

    private final Supplier<ParallaxBackground> background;

    private DynamicShaderProgram dynamicShaderProgram;

    private DynamicVAO dynamicVao;

    public ParallaxBackgroundRenderPass(Supplier<ParallaxBackground> background) {
        this.background = background;
    }

    @Override
    public void init(CurrentContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            dynamicShaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        dynamicVao = new DynamicVAO();
    }

    @Override
    public void render(CurrentContext renderContext, Engine engine, Window window, double deltaTime) {
        dynamicShaderProgram.bind();
        dynamicShaderProgram.setProjectionMatrix(renderContext.getProjection());
        dynamicShaderProgram.setModelMatrix(Transformable.empty());

        for (ParallaxLayer layer : background.get().getLayers()) {
            for (ParallaxObject object : layer.getObjects()) {
                object.texture.bind();

                float width, height;

                switch (object.getSizeType()) {
                    case SCREEN_SIZE:
                        float aspectRatio = (float) object.texture.getWidth() / (float) object.texture.getHeight();
                        width = window.getWidth() * aspectRatio;
                        height = window.getHeight();
                        break;
                    case OBJECT_SIZE:
                        width  = object.texture.getWidth()  * object.getScale();
                        height = object.texture.getHeight() * object.getScale();
                        break;
                    default:
                        width = 0;
                        height = 0;
                }

                switch (object.getDrawType()) {
                    case REPEAT:
                        float x = object.getOffsetX();
                        float y = object.getOffsetY();
                        dynamicVao.drawRect(0, 0, width, height,
                                x, y, x + 1, y + 1,
                                1F, 1F, 1F, 1F);
                        break;
                    case STATIC:
                        dynamicVao.drawRect(0, 0, width, height,
                                0, 0, 1, 1,
                                1F, 1F, 1F, 1F);
                        break;
                }
            }
        }

        ShaderProgram.unbind();
    }
}