package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.cubes.CubesScene;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class GridCellRenderPass extends RenderPass<RenderContext> {

    private final Vector4f color = new Vector4f(1F, 0F, 0F, 1F);

    private final CubesScene scene;

    private final Supplier<Camera> camera;

    private DynamicShaderProgram shaderProgram;

    public GridCellRenderPass(CubesScene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setModelMatrix(Transformable.empty());
        shaderProgram.setColor(color);

        MeshRenderer.render(scene.getGridCellMesh().getMesh(), null);
    }
}
