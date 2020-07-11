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
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.fog.pipeline.shaders.TemplateShaderPatches;
import org.joml.Matrix4f;

import java.util.function.Supplier;


/**
 * @author Brandon
 * @since September 04, 2019
 */
public class TemplateModelRenderPass extends RenderPass<RenderContext> {

    private static final String lightName = "sun";
    private static final String materialName = "material";

    private ShaderProgram shaderProgram;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher matcher;

    private final Supplier<Fog> fog;

    private final Supplier<DirectionalLight> light;

    private final Matrix4f modelMatrix;

    public TemplateModelRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Fog> fog,
                                   Supplier<DirectionalLight> light) {
        this.scene = scene;
        this.camera = camera;
        this.fog = fog;
        this.light = light;
        this.modelMatrix = new Matrix4f();
        this.matcher = scene.getEs().matcher(Model.class, Transformable.class, Float.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = TemplateShaderPatches.buildTemplateShaderProgram(engine.getContext(),
                    VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4, materialName, lightName);
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

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            Model model = entity.getComponent(Model.class);
            Transformable transformable = entity.getComponent(Transformable.class);

            TemplateShaderPatches.setModelMatrix(shaderProgram, transformable, modelMatrix);



            for (Material material : model.getData().keySet()) {
                MaterialBinder.bindMaterialTextures(material);
                MaterialBinder.setMaterialUniforms(shaderProgram, material, material.getMaterialFormat(), materialName);

                for (Mesh mesh : model.getData().get(material)) {
                    ModelRenderer.renderMesh(mesh);
                }
            }
        }

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
    }
}
