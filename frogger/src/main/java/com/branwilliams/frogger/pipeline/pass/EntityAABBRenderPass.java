package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.sprite.Sprite;
import org.joml.Vector2f;

import java.util.function.Supplier;

public class EntityAABBRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Vector2f> focalPoint;

    private final IComponentMatcher matcher;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    public EntityAABBRenderPass(Scene scene) {
        this(scene, Vector2f::new);
    }

    public EntityAABBRenderPass(Scene scene, Supplier<Vector2f> focalPoint) {
        this.scene = scene;
        this.focalPoint = focalPoint;
        this.matcher = scene.getEs().matcher(AABB2f.class, Transformable.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_2D_COLOR);
            this.dynamicVAO = new DynamicVAO(VertexFormat.POSITION_2D_COLOR);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());

        dynamicVAO.begin();
        for (IEntity entity : scene.getEs().getEntities(matcher)) {
//            Transformable transformable = entity.getComponent(Transformable.class);
            Transformable transformable = Transformable.empty();
            shaderProgram.setModelMatrix(transformable);

            AABB2f aabb = entity.getComponent(AABB2f.class);
            dynamicVAO.addRect(aabb.getMinX(), aabb.getMinY(), aabb.getMaxX(), aabb.getMaxY(), 0xFFFFFFFF);
        }
        dynamicVAO.draw();

        ShaderProgram.unbind();
    }
}
