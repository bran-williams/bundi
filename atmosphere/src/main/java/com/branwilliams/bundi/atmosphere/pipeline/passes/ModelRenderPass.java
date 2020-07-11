package com.branwilliams.bundi.atmosphere.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 17, 2019
 */
public class ModelRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private DynamicShaderProgram shaderProgram;


    public ModelRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, Model.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());

        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            Model model = entity.getComponent(Model.class);
            shaderProgram.setModelMatrix(transformable);

            ModelRenderer.renderModel(model);
        }

        ShaderProgram.unbind();
    }
}
