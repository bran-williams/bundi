package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
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
import com.branwilliams.cubes.Grid3f;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class GridCellRenderPass extends RenderPass<RenderContext> {

    private final Vector4f color = new Vector4f(1F, 0F, 0F, 1F);

    private final CubesScene scene;

    private final Supplier<DirectionalLight> sun;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private CubesShaderProgram shaderProgram;

    public GridCellRenderPass(CubesScene scene, Supplier<DirectionalLight> sun, Supplier<Camera> camera) {
        this.scene = scene;
        this.sun = sun;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, GridCellMesh.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new CubesShaderProgram(engine.getContext());
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
        shaderProgram.setTextureColor(color);
        shaderProgram.setLight(sun.get());

        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            GridCellMesh mesh = entity.getComponent(GridCellMesh.class);
            shaderProgram.setModelMatrix(transformable);
            MeshRenderer.render(mesh.getMesh(), null);
        }
    }
}
