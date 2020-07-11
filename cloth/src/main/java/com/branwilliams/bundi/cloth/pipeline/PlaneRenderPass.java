package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import org.joml.Planef;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class PlaneRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<Planef> plane;

    private DynamicShaderProgram shaderProgram;

//    private SphereMesh sphereMesh;

    private Transformable transformable = new Transformation();

    private Vector4f color = new Vector4f(0F, 0F, 1.0F, 1.0F);

    public PlaneRenderPass(Supplier<Camera> camera, Supplier<Planef> plane) {
        this.camera = camera;
        this.plane = plane;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create ClothShaderProgram: ", e);
        }
//        sphereMesh = new SphereMesh(1.0F, 90, 90, VertexFormat.POSITION);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setColor(color);
//        transformable.position(sphere.get().x, sphere.get().y, sphere.get().z).scale(sphere.get().r);
//        shaderProgram.setModelMatrix(transformable);
//
//        MeshRenderer.render(sphereMesh, null);

        ShaderProgram.unbind();
    }
}
