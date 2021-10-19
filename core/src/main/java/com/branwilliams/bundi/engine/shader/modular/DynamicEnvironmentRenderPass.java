package com.branwilliams.bundi.engine.shader.modular;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.module.EnvironmentShaderModule;
import com.branwilliams.bundi.engine.util.Tuple;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DynamicEnvironmentRenderPass extends RenderPass<RenderContext> implements IComponentMatcher {

    private final Map<Tuple<VertexFormat<?>, MaterialFormat>, ModularShaderProgram> shaders;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final Supplier<Environment> environment;

    private final IComponentMatcher componentMatcher;

    public DynamicEnvironmentRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Environment> environment) {
        this.shaders = new HashMap<>();
        this.scene = scene;
        this.camera = camera;
        this.environment = environment;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, Mesh.class, Material.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {

    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        Map<Tuple<VertexFormat<?>, MaterialFormat>, List<IEntity>> mappedEntities = new HashMap<>();
        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            Tuple<VertexFormat<?>, MaterialFormat> key = new Tuple<>(entity.getComponent(Mesh.class).getVertexFormat(),
                    entity.getComponent(Material.class).getMaterialFormat());
            if (!EnvironmentShaderModule.isVertexFormatValidForEnvironment(key.getA(), environment)) {
                continue;
            }
            if (!mappedEntities.containsKey(key)) {
                mappedEntities.put(key, new ArrayList<>());
            }
            mappedEntities.get(key).add(entity);
        }

        for (Tuple<VertexFormat<?>, MaterialFormat> key : mappedEntities.keySet()) {
            if (!shaders.containsKey(key)) {
                createShader(key);
            }

            ModularShaderProgram shaderProgram = shaders.get(key);

            shaderProgram.bind();
            shaderProgram.update(renderContext.getProjection(), camera.get());

            for (IEntity entity : mappedEntities.get(key)) {
                Mesh mesh = entity.getComponent(Mesh.class);
                Transformable transformable = entity.getComponent(Transformable.class);
                Material material = entity.getComponent(Material.class);

                shaderProgram.setModelMatrix(transformable);
                shaderProgram.setMaterial(material);

                MeshRenderer.render(mesh, null);
            }
        }
    }

    private void createShader(Tuple<VertexFormat<?>, MaterialFormat> key) {
        try {
            EnvironmentShaderModule environmentShaderModule = new EnvironmentShaderModule(environment, key.getA(),
                    key.getB());
            ModularShaderProgram shaderProgram = new ModularShaderProgram(key.getA(), key.getB(),
                    Lists.newArrayList(environmentShaderModule));
            shaders.put(key, shaderProgram);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean matches(IEntity entity) {
        return false;
    }
}
