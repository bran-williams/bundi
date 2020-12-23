package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.frogger.components.ScaledTexture;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLayer;
import com.branwilliams.frogger.parallax.ParallaxObject;
import com.branwilliams.frogger.pipeline.shader.ParallaxBackgroundShaderProgram;

import java.util.function.Supplier;

public class ParallaxBackgroundRenderPass<CurrentContext extends RenderContext> extends RenderPass<CurrentContext> {

    private final Transformable transform = new Transformation();

    private final Supplier<ParallaxBackground<ScaledTexture>> background;

    private ParallaxBackgroundShaderProgram parallaxShaderProgram;

    private DynamicVAO dynamicVao;

    public ParallaxBackgroundRenderPass(Supplier<ParallaxBackground<ScaledTexture>> background) {
        this.background = background;
    }

    @Override
    public void init(CurrentContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            parallaxShaderProgram = new ParallaxBackgroundShaderProgram(engine.getContext());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        dynamicVao = new DynamicVAO();
        dynamicVao.begin();
        dynamicVao.addRect(0F, 0F, 1F, 1F,
                0F, 0F, 1F, 1F,
                1F, 1F, 1F, 1F);
        dynamicVao.compile();
    }

    @Override
    public void render(CurrentContext renderContext, Engine engine, Window window, double deltaTime) {
        parallaxShaderProgram.bind();
        parallaxShaderProgram.setProjectionMatrix(renderContext.getProjection());

        for (ParallaxLayer<ScaledTexture> layer : background.get().getLayers()) {
            for (ParallaxObject<ScaledTexture> object : layer.getObjects()) {

                parallaxShaderProgram.setModelMatrix(transform.scale(object.getObject().getScale()));
                object.getObject().getTextureObject().bind();

                parallaxShaderProgram.setParallaxObject(object);

                dynamicVao.draw();
            }
        }

        ShaderProgram.unbind();
    }
}