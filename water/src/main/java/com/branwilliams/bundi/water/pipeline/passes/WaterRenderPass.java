package com.branwilliams.bundi.water.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.water.Water;
import com.branwilliams.bundi.water.pipeline.shaders.WaterShaderProgram;

import java.util.function.Supplier;


/**
 * @author Brandon
 * @since September 04, 2019
 */
public class WaterRenderPass extends RenderPass<RenderContext> {

    private WaterShaderProgram shaderProgram;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher matcher;

    public WaterRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.matcher = scene.getEs().matcher(Water.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new WaterShaderProgram(engine.getContext());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            Water water = entity.getComponent(Water.class);

            shaderProgram.setModelMatrix(water.getTransformable());
            shaderProgram.setWater(water);

            MeshRenderer.render(water.getWaterMesh(), water.getMaterial());
        }

        ShaderProgram.unbind();
    }
}
