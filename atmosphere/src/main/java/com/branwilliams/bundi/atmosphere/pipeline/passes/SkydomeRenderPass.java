package com.branwilliams.bundi.atmosphere.pipeline.passes;

import com.branwilliams.bundi.atmosphere.Skydome;
import com.branwilliams.bundi.atmosphere.pipeline.shaders.SkydomeShaderProgram;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;

import java.util.function.Supplier;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class SkydomeRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<Skydome> skydome;

    private SkydomeShaderProgram skydomeShaderProgram;

    public SkydomeRenderPass(Supplier<Camera> camera, Supplier<Skydome> skydome) {
        this.camera = camera;
        this.skydome = skydome;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.skydomeShaderProgram = new SkydomeShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create scene shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        // Bind skydome shader program and render skydome.
        this.skydomeShaderProgram.bind();
        this.skydomeShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.skydomeShaderProgram.setViewMatrix(camera.get());
        Skydome skydome = this.skydome.get();
        this.skydomeShaderProgram.setColors(skydome.getApexColor(), skydome.getCenterColor());
        MeshRenderer.render(skydome.getSkydomeMesh(), null);
    }

}
