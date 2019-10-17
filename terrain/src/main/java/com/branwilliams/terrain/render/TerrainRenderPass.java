package com.branwilliams.terrain.render;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.terrain.TerrainTile;

import java.util.function.Supplier;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class TerrainRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final Supplier<Camera> cameraSupplier;

    private final IComponentMatcher componentMatcher;

    private TerrainShaderProgram2 terrainShaderProgram;

    public TerrainRenderPass(Scene scene, Supplier<Camera> cameraSupplier) {
        this.scene = scene;
        this.cameraSupplier = cameraSupplier;
        componentMatcher = scene.getEs().matcher(TerrainTile.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            terrainShaderProgram = new TerrainShaderProgram2(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create terrain shader program!");
            throw new InitializationException(e);
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        this.terrainShaderProgram.bind();
        this.terrainShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.terrainShaderProgram.setViewMatrix(cameraSupplier.get());

        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            TerrainTile terrainTile = entity.getComponent(TerrainTile.class);

            this.terrainShaderProgram.setModelMatrix(terrainTile.getTransform());
            this.terrainShaderProgram.setMaterial(terrainTile.getMaterial());

            MeshRenderer.render(terrainTile.getMesh(), terrainTile.getMaterial());
        }
    }

}
