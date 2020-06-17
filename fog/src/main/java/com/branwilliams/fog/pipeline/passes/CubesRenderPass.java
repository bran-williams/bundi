package com.branwilliams.fog.pipeline.passes;

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
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.fog.pipeline.shaders.TemplateShaderPatches;

import java.awt.*;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;


/**
 * @author Brandon
 * @since September 04, 2019
 */
public class CubesRenderPass extends RenderPass<RenderContext> {

    private DynamicShaderProgram shaderProgram;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher matcher;

    private final Supplier<Fog> fog;

    public CubesRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Fog> fog) {
        this.scene = scene;
        this.camera = camera;
        this.fog = fog;
        this.matcher = scene.getEs().matcher(Mesh.class, Transformable.class, Color.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX | DynamicShaderProgram.FOG);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
        shaderProgram.setFog(fog.get());

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            Mesh mesh = entity.getComponent(Mesh.class);
            Transformable transformable = entity.getComponent(Transformable.class);
            Color color = entity.getComponent(Color.class);

            shaderProgram.setModelMatrix(transformable);
            shaderProgram.setColor(toVector4(color));

            MeshRenderer.render(mesh, null);
        }

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
    }
}
