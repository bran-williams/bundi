package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<Cloth> cloth;

    private ClothShaderProgram shaderProgram;

    private Transformable transformable = new Transformation();


    private DirectionalLight sun = new DirectionalLight(
            new Vector3f(-0.2F, -1F, -0.3F), // direction
            new Vector3f(0.5F),                      // ambient
            new Vector3f(0.4F),    // diffuse
            new Vector3f(0.5F));                       // specular

    public ClothRenderPass(Supplier<Camera> camera, Supplier<Cloth> cloth) {
        this.camera = camera;
        this.cloth = cloth;
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
        shaderProgram.setModelMatrix(transformable);
        shaderProgram.setLight(sun);
        MeshRenderer.render(cloth.get().getMesh(), cloth.get().getMaterial());

        ShaderProgram.unbind();
    }
}
