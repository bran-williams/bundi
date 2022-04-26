package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderProgram;
import com.branwilliams.bundi.engine.shader.modular.module.EnvironmentShaderModule;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;


public class CowRenderPass extends RenderPass<VoxelRenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private ModularShaderProgram shaderProgram;

    private Environment environment = new Environment(new Fog(0.025F),
            new PointLight[]{}, new DirectionalLight[] {
                    new DirectionalLight(
                            new Vector3f(-0.2F, -1.0F, -0.3F),// direction
                            new Vector3f(0.2F),                     // ambient
                            new Vector3f(0.4F),                     // diffuse
                            new Vector3f(0.5F))                     // specular
    }, new SpotLight[]{});

    public CowRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, Model.class);
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            EnvironmentShaderModule environmentShaderModule = new EnvironmentShaderModule(() -> environment,
                    VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_SAMPLER2D);
            shaderProgram = new ModularShaderProgram(VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_SAMPLER2D,
                    Collections.singletonList(environmentShaderModule));
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.update(renderContext.getProjection(), camera.get());

        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            Model model = entity.getComponent(Model.class);
            shaderProgram.setModelMatrix(transformable);

            ModelRenderer.renderModel(model);
        }

        ShaderProgram.unbind();
    }
}
