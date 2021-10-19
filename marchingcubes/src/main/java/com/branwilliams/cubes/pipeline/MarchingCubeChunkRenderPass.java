package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderProgram;
import com.branwilliams.bundi.engine.shader.modular.module.EnvironmentShaderModule;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import com.google.common.collect.Lists;
import org.joml.Vector4f;

import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.ColorUtils.*;
import static com.branwilliams.cubes.CubesScene.WORLD_COLOR;

public class MarchingCubeChunkRenderPass extends RenderPass<RenderContext> {

    private final CubesScene scene;

    private final Supplier<Environment> environment;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private ModularShaderProgram shaderProgram;

    private Frustum frustum;

    public MarchingCubeChunkRenderPass(CubesScene scene, Supplier<Environment> environment, Supplier<Camera> camera) {
        this.scene = scene;
        this.environment = environment;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(Transformable.class, MarchingCubeChunk.class, Material.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            frustum = new Frustum();
            EnvironmentShaderModule environmentShaderModule = new EnvironmentShaderModule(environment,
                    VertexFormat.POSITION_NORMAL, MaterialFormat.DIFFUSE_NORMAL_SPECULAR);
            shaderProgram = new ModularShaderProgram(VertexFormat.POSITION_NORMAL,
                    MaterialFormat.DIFFUSE_NORMAL_SPECULAR, Lists.newArrayList(environmentShaderModule));
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.update(renderContext.getProjection(), camera.get());
        frustum.update(renderContext.getProjection(), camera.get());

        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            MarchingCubeChunk chunk = entity.getComponent(MarchingCubeChunk.class);
            if (frustum.insideFrustumAABB(chunk.getBounds())) {
                Transformable transformable = entity.getComponent(Transformable.class);
                Material material = entity.getComponent(Material.class);

                shaderProgram.setModelMatrix(transformable);
                shaderProgram.setMaterial(material);
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
