package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.DebugOriginMesh;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import org.joml.Vector4f;

import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;
import static com.branwilliams.cubes.CubesScene.WORLD_COLOR;

public class DebugOriginRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private DynamicShaderProgram shaderProgram;

    private Transformable transformable = new Transformation();

    public DebugOriginRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(DebugOriginMesh.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_COLOR, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            DebugOriginMesh debugOriginMesh = entity.getComponent(DebugOriginMesh.class);
            shaderProgram.setModelMatrix(Transformable.empty());
//            shaderProgram.setModelMatrix(transformable.position(debugOriginMesh.getOrigin()));
            MeshRenderer.render(debugOriginMesh.getMesh(), null);
        }
    }
}
