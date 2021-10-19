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
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.frogger.tilemap.Tilemap;
import org.joml.Vector2f;

import java.util.function.Supplier;

import static com.branwilliams.frogger.FroggerConstants.FROGMAN_NAME;

public class SpriteCollisionAABBRenderPass extends RenderPass<RenderContext> {

    private final Transformable transform = new Transformation();

    private final Scene scene;

    private final Supplier<Vector2f> focalPoint;

    private final IComponentMatcher matcher;

    private final Supplier<Tilemap> tilemap;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;


    public SpriteCollisionAABBRenderPass(Scene scene, Supplier<Vector2f> focalPoint, Supplier<Tilemap> tilemap) {
        this.scene = scene;
        this.focalPoint = focalPoint;
        this.tilemap = tilemap;
        this.matcher = scene.getEs().matcher(FROGMAN_NAME);
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
        shaderProgram.setModelMatrix(transform.position(-focalPoint.get().x, -focalPoint.get().y, 0F));
        dynamicVAO.begin();
        Tilemap tilemap = this.tilemap.get();
        for (IEntity frogman : scene.getEs().getEntities(matcher)) {
            Transformable frogmanTransform = frogman.getComponent(Transformable.class);
            AABB2f frogmanAABB = frogman.getComponent(AABB2f.class);
            tilemap.forTilesInRange(frogmanAABB, (x, y, tile) -> {
//                System.out.println("frogman collision: x=" + x + ", y=" + y);
                dynamicVAO.addRect(x * tilemap.getTileWidth(), y * tilemap.getTileHeight(),
                        x * tilemap.getTileWidth()  + tilemap.getTileWidth(), y * tilemap.getTileHeight() + tilemap.getTileHeight(),
                        0xFFFF0000);
            });
        }
        dynamicVAO.draw();

        ShaderProgram.unbind();
    }
}
