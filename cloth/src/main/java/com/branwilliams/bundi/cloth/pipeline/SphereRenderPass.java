package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import org.joml.Spheref;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class SphereRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<Spheref> sphere;

    private DynamicShaderProgram shaderProgram;

    private SphereMesh sphereMesh;

    private Transformable transformable = new Transformation();

    private Vector4f color = new Vector4f(0F, 0F, 1.0F, 1.0F);

    public SphereRenderPass(Supplier<Camera> camera, Supplier<Spheref> sphere) {
        this.camera = camera;
        this.sphere = sphere;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            throw new InitializationException("Unable to create ClothShaderProgram: ", e);
        }
        sphereMesh = new SphereMesh(1.0F, 32, 32, VertexFormat.POSITION);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setColor(color);
        transformable.position(sphere.get().x, sphere.get().y, sphere.get().z).scale(sphere.get().r);
        shaderProgram.setModelMatrix(transformable);

        MeshRenderer.render(sphereMesh, null);

        ShaderProgram.unbind();
    }
}
