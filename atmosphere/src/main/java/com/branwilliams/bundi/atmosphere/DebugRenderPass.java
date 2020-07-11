package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.mesh.primitive.GridMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public class DebugRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private DynamicShaderProgram shaderProgram;

    private GridMesh gridMesh;

    public DebugRenderPass(Supplier<Camera> camera) {
        this.camera = camera;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_COLOR, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        gridMesh = new GridMesh(64, 8F);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setModelMatrix(Transformable.empty());
        MeshRenderer.render(gridMesh, null);
        ShaderProgram.unbind();
    }
}
