package com.branwilliams.fog.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialBinder;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

import java.util.function.Supplier;


/**
 * @author Brandon
 * @since September 04, 2019
 */
public class EnvironmentRenderPass extends RenderPass<RenderContext> implements IComponentMatcher {

    private EnvironmentShaderProgram shaderProgram;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final Supplier<Environment> environment;

    private final VertexFormat<?> vertexFormat;

    private final MaterialFormat materialFormat;

    public EnvironmentRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Environment> environment,
                                 VertexFormat<?> vertexFormat, MaterialFormat materialFormat) {
        this.scene = scene;
        this.camera = camera;
        this.environment = environment;
        this.vertexFormat = vertexFormat;
        this.materialFormat = materialFormat;
        scene.getEs().addMatcher(this);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new EnvironmentShaderProgram(engine.getContext(), vertexFormat, materialFormat,
                    environment.get());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setModelMatrix(Transformable.empty());
        shaderProgram.setEnvironment(environment.get());

        for (IEntity entity : scene.getEs().getEntities(this)) {
            Mesh mesh = entity.getComponent(Mesh.class);
            Transformable transformable = entity.getComponent(Transformable.class);
            Material material = entity.getComponent(Material.class);

            shaderProgram.setModelMatrix(transformable);

            MaterialBinder.bindMaterialTextures(material);
            shaderProgram.setMaterial(material);

            MeshRenderer.render(mesh, null);
        }

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
    }

    @Override
    public boolean matches(IEntity entity) {
        if (!entity.hasComponent(Transformable.class)
                || !entity.hasComponent(Mesh.class)
                || !entity.hasComponent(Material.class))
            return false;

        Mesh mesh = entity.getComponent(Mesh.class);
        Material material = entity.getComponent(Material.class);

        return material.getMaterialFormat() != null
                && material.getMaterialFormat().equals(this.materialFormat)
                && mesh.getVertexFormat() != null
                && mesh.getVertexFormat().equals(this.vertexFormat);
    }
}
