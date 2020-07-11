package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import org.joml.Vector4f;

import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.ColorUtils.*;
import static com.branwilliams.cubes.CubesScene.WORLD_COLOR;

public class GridCellRenderPass extends RenderPass<RenderContext> {

    private final Vector4f color = toVector4(WORLD_COLOR);

    private final CubesScene scene;

    private final Supplier<DirectionalLight> sun;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private CubesShaderProgram shaderProgram;

    public GridCellRenderPass(CubesScene scene, Supplier<DirectionalLight> sun, Supplier<Camera> camera) {
        this.scene = scene;
        this.sun = sun;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, MarchingCubeChunk.class);
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
            MarchingCubeChunk chunk = entity.getComponent(MarchingCubeChunk.class);
            if (chunk.getGridCellMesh().getMesh().getVertexCount() != 0) {
                Transformable transformable = entity.getComponent(Transformable.class);
                shaderProgram.setModelMatrix(transformable);
                MeshRenderer.render(chunk.getGridCellMesh().getMesh(), null);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
    }
}
