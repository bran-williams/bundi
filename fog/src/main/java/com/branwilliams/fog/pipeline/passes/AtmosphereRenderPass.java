package com.branwilliams.fog.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.fog.Atmosphere;
import com.branwilliams.fog.pipeline.shaders.AtmosphereShaderProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class AtmosphereRenderPass <MyRenderContext extends RenderContext> extends RenderPass<MyRenderContext> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AtmosphereShaderProgram atmosphereShaderProgram;

    private final Supplier<Mesh> skydome;

    private final Supplier<Camera> camera;

    private final Supplier<Atmosphere> atmosphere;

    public AtmosphereRenderPass(Supplier<Camera> camera, Supplier<Atmosphere> atmosphere, Supplier<Mesh> skydome) {
        this.camera = camera;
        this.atmosphere = atmosphere;
        this.skydome = skydome;
    }

    @Override
    public void init(MyRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.atmosphereShaderProgram = new AtmosphereShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            log.error("Unable to create skybox shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(MyRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        // Bind skybox shader program and render skybox.
        this.atmosphereShaderProgram.bind();
        this.atmosphereShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.atmosphereShaderProgram.setViewMatrix(this.camera.get());
        this.atmosphereShaderProgram.setAtmosphere(this.atmosphere.get());
        Mesh skydome = this.skydome.get();
        MeshRenderer.render(skydome, null);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.atmosphereShaderProgram.destroy();
    }
}
