package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.mesh.primitive.GridMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.cubes.DebugGridMesh;
import com.branwilliams.cubes.DebugOriginMesh;
import com.branwilliams.cubes.builder.DebugOriginMeshBuilderImpl;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class DebugRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private DynamicShaderProgram shaderProgram;

    private Transformable transformable = new Transformation();

    private DebugOriginMesh originMesh;

    private Mesh gridMesh;

    public DebugRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(DebugOriginMesh.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_COLOR, DynamicShaderProgram.VIEW_MATRIX);

            DebugOriginMeshBuilderImpl debugOriginMeshBuilder = new DebugOriginMeshBuilderImpl();
            originMesh = debugOriginMeshBuilder.buildMesh(new Vector3f(0, 0.01F,0), 10);
            gridMesh = new DebugGridMesh(new Vector3f(), 256, 1);
//            gridMesh = new GridMesh(256, 512);

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
        MeshRenderer.render(gridMesh, null);
        MeshRenderer.render(originMesh.getMesh(), null);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
        this.gridMesh.destroy();
        this.originMesh.destroy();
    }
}
