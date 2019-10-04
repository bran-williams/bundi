package com.branwilliams.bundi.engine.skybox;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class SkyboxRenderPass <RenderContextType extends RenderContext> extends RenderPass<RenderContextType> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Supplier<Camera> camera;

    private final Supplier<Skybox> skybox;

    private SkyboxShaderProgram skyboxShaderProgram;

    public SkyboxRenderPass(Supplier<Camera> camera, Supplier<Skybox> skybox) {
        this.camera = camera;
        this.skybox = skybox;
    }

    @Override
    public void init(RenderContextType renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.skyboxShaderProgram = new SkyboxShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            log.error("Unable to create skybox shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(RenderContextType renderContext, Engine engine, Window window, double deltaTime) {
        // Bind skybox shader program and render skybox.
        this.skyboxShaderProgram.bind();
        this.skyboxShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.skyboxShaderProgram.setViewMatrix(camera.get());
        Skybox skybox = this.skybox.get();
        MeshRenderer.render(skybox.getMesh(), skybox.getMaterial());
    }
}
