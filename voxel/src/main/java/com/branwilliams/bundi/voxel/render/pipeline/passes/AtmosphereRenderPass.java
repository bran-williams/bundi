package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.AtmosphereShaderProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class AtmosphereRenderPass extends RenderPass<VoxelRenderContext> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VoxelScene scene;

    private AtmosphereShaderProgram atmosphereShaderProgram;

    public AtmosphereRenderPass(VoxelScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.atmosphereShaderProgram = new AtmosphereShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            log.error("Unable to create skybox shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        // Bind skybox shader program and render skybox.
        this.atmosphereShaderProgram.bind();
        this.atmosphereShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.atmosphereShaderProgram.setViewMatrix(this.scene.getCamera());
        this.atmosphereShaderProgram.setAtmosphere(this.scene.getAtmosphere());
        Skybox skybox = this.scene.getSkybox();
        MeshRenderer.render(skybox.getMesh(), skybox.getMaterial());
    }

    @Override
    public void destroy() {
        super.destroy();
        this.atmosphereShaderProgram.destroy();
    }
}
