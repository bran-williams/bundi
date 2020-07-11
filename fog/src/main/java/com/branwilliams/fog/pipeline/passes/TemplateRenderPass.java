package com.branwilliams.fog.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialBinder;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.fog.pipeline.shaders.TemplateShaderPatches;
import org.joml.Matrix4f;

import java.util.function.Supplier;


/**
 * @author Brandon
 * @since September 04, 2019
 */
public class TemplateRenderPass extends RenderPass<RenderContext> implements IComponentMatcher {

    private static final String lightName = "sun";
    private static final String materialName = "material";

    private ShaderProgram shaderProgram;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final Supplier<Fog> fog;

    private final Supplier<DirectionalLight> light;

    private final VertexFormat vertexFormat;

    private final MaterialFormat materialFormat;

    private final Matrix4f modelMatrix;

    public TemplateRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Fog> fog,
                              Supplier<DirectionalLight> light, VertexFormat vertexFormat,
                              MaterialFormat materialFormat) {
        this.scene = scene;
        this.camera = camera;
        this.fog = fog;
        this.light = light;
        this.vertexFormat = vertexFormat;
        this.materialFormat = materialFormat;
        this.modelMatrix = new Matrix4f();
        scene.getEs().addMatcher(this);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = TemplateShaderPatches.buildTemplateShaderProgram(engine.getContext(),
                    vertexFormat, materialFormat, materialName, lightName);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();

        TemplateShaderPatches.setProjectionMatrix(shaderProgram, renderContext.getProjection());
        TemplateShaderPatches.setViewMatrix(shaderProgram, camera.get());
        TemplateShaderPatches.setViewPos(shaderProgram, camera.get());
        TemplateShaderPatches.setDirectionalLight(shaderProgram, light.get(), lightName);
        TemplateShaderPatches.setFog(shaderProgram, fog.get());

        for (IEntity entity : scene.getEs().getEntities(this)) {
            Mesh mesh = entity.getComponent(Mesh.class);
            Transformable transformable = entity.getComponent(Transformable.class);
            Material material = entity.getComponent(Material.class);

            TemplateShaderPatches.setModelMatrix(shaderProgram, transformable, modelMatrix);

            MaterialBinder.bindMaterialTextures(material);
            MaterialBinder.setMaterialUniforms(shaderProgram, material, material.getMaterialFormat(), materialName);

            MeshRenderer.render(mesh, null);
        }

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
    }

    @Override
    public boolean matches(IEntity entity) {
        if (!entity.hasComponent(Transformable.class)
                || !entity.hasComponent(Mesh.class)
                || !entity.hasComponent(Material.class))
            return false;

        Mesh mesh = entity.getComponent(Mesh.class);
        Material material = entity.getComponent(Material.class);

        return material.getMaterialFormat() != null
                && material.getMaterialFormat().equals(this.materialFormat)
                && mesh.getVertexFormat() != null
                && mesh.getVertexFormat().equals(this.vertexFormat);
    }
}
