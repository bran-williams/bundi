package com.branwilliams.demo.template2d.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.sprite.Sprite;

public class SpriteRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final IComponentMatcher matcher;

    private DynamicShaderProgram shaderProgram;

    public SpriteRenderPass(Scene scene) {
        this.scene = scene;
        this.matcher = scene.getEs().matcher(Sprite.class, Transformable.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            shaderProgram.setModelMatrix(transformable);

            Sprite sprite = entity.getComponent(Sprite.class);
            sprite.draw();
        }

        ShaderProgram.unbind();
    }
}
