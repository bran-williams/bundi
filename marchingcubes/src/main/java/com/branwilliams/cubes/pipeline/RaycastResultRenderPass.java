package com.branwilliams.cubes.pipeline;

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
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.math.RaycastResult;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.MeshUtils.calculateNormal;

public class RaycastResultRenderPass extends RenderPass<RenderContext> {

    private final Transformable transformable = new Transformation();

    private final Vector4f red = new Vector4f(0F, 1F, 0F, 0.6F);

    private final Vector4f green = new Vector4f(0F, 1F, 0F, 0.6F);

    private final Vector4f blue = new Vector4f(0F, 0F, 1F, 0.6F);

    private final CubesScene scene;

    private final Supplier<Camera> camera;

    private DynamicShaderProgram shaderProgram;

    private SphereMesh sphereMesh;

    public RaycastResultRenderPass(CubesScene scene, Supplier<Camera> camera) {
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
        this.sphereMesh = new SphereMesh(1F, 90, 90, VertexFormat.POSITION);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());

        RaycastResult raycast = scene.getRaycast();
        if (raycast != null) {
            shaderProgram.setModelMatrix(transformable.position(raycast.position));
            shaderProgram.setColor(green);
            MeshRenderer.render(sphereMesh, null);
        }
        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
        this.sphereMesh.destroy();
    }
}
