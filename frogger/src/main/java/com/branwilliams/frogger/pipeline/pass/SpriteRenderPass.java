package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.sprite.Sprite;
import org.joml.Vector2f;

import java.util.function.Supplier;

public class SpriteRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Vector2f> focalPoint;

    private final IComponentMatcher matcher;

    private DynamicShaderProgram shaderProgram;

    private Transformable transform;

    public SpriteRenderPass(Scene scene) {
        this(scene, Vector2f::new);
    }

    public SpriteRenderPass(Scene scene, Supplier<Vector2f> focalPoint) {
        this.scene = scene;
        this.focalPoint = focalPoint;
        this.matcher = scene.getEs().matcher(Sprite.class, Transformable.class);
        this.transform = new Transformation();
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

        Vector2f focalPoint = this.focalPoint.get();
        for (IEntity entity : scene.getEs().getEntities(matcher)) {
//            shaderProgram.setModelMatrix(entity.getComponent(Transformable.class));
            shaderProgram.setModelMatrix(entity.getComponent(Transformable.class).copy().move(-focalPoint.x, -focalPoint.y, 0));

            Sprite sprite = entity.getComponent(Sprite.class);
            sprite.draw();
        }

        ShaderProgram.unbind();
    }
}
