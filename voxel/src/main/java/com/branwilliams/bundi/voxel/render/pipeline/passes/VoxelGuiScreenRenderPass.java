package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.scene.VoxelScene;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelGuiScreenRenderPass extends RenderPass<VoxelRenderContext> {

    private final VoxelScene scene;

    private DynamicShaderProgram dynamicShaderProgram;


    public VoxelGuiScreenRenderPass(VoxelScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            dynamicShaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (scene.getGuiScreen() != null) {
            dynamicShaderProgram.bind();
            dynamicShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
            dynamicShaderProgram.setModelMatrix(Transformable.empty());
            scene.getGuiScreen().render();
            ShaderProgram.unbind();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.dynamicShaderProgram.destroy();
    }
}
