package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import org.joml.Vector3f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Scene scene;

    private final IComponentMatcher matcher;

    private ClothShaderProgram shaderProgram;

    private DirectionalLight sun = new DirectionalLight(
            new Vector3f(-0.2F, -1F, -0.3F), // direction
            new Vector3f(0.5F),                      // ambient
            new Vector3f(0.4F),    // diffuse
            new Vector3f(0.5F));                       // specular


    public ClothRenderPass(Supplier<Camera> camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
        this.matcher = scene.getEs().matcher(Transformable.class, Mesh.class, Material.class, Cloth.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new ClothShaderProgram(engine.getContext());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create ClothShaderProgram: ", e);
        }

    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            shaderProgram.setModelMatrix(entity.getComponent(Transformable.class));
            shaderProgram.setLight(sun);
            MeshRenderer.render(entity.getComponent(Mesh.class), entity.getComponent(Material.class));
        }

        ShaderProgram.unbind();
    }
}
