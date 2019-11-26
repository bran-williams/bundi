package com.branwilliams.bundi.model.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

import java.util.function.Supplier;

public class ModelRenderPass extends RenderPass {

    private final Supplier<Camera> camera;

    private final VertexFormat vertexFormat;

    private DynamicShaderProgram shaderProgram;

    private final Supplier<Model> model;

    private final Supplier<Transformable> transform;

    public ModelRenderPass(Supplier<Camera> camera, VertexFormat vertexFormat, Supplier<Transformable> transform,
                           Supplier<Model> model) {
        this.camera = camera;
        this.vertexFormat = vertexFormat;
        this.model = model;
        this.transform = transform;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(vertexFormat, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        Model model = this.model.get();
        Transformable transform = this.transform.get();

        shaderProgram.bind();
//        shaderProgram.setColor(new Vector4f(1F));
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setModelMatrix(transform);

        ModelRenderer.renderModel(model);

        ShaderProgram.unbind();
    }
}
