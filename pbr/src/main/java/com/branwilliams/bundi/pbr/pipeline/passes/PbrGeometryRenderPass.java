package com.branwilliams.bundi.pbr.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.pbr.pipeline.shaders.PbrGeometryShaderProgram;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderContext;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class PbrGeometryRenderPass extends RenderPass<PbrRenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private PbrGeometryShaderProgram geometryShaderProgram;

    public PbrGeometryRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        componentMatcher = scene.getEs().matcher(Transformable.class, Model.class);
    }

    @Override
    public void init(PbrRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            geometryShaderProgram = new PbrGeometryShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create shader program!");
            throw new InitializationException(e);
        }
    }

    /**
     * 1. Binds GBuffer, disables blending, enables depth testing, and enables depth writing.
     * 2. Render each object within the scene.
     * */
    @Override
    public void render(PbrRenderContext renderContext, Engine engine, Window window) {
        renderContext.getGBuffer().bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // No blending required.
        glDisable(GL_BLEND);

        // Write to the depth buffer.
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
         
//        if (scene.isWireframe())
//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind mesh shader program.
        this.geometryShaderProgram.bind();
        this.geometryShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.geometryShaderProgram.setViewMatrix(camera.get());

        // Render each entity within scene.
        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            Transformable transformable = entity.getComponent(Transformable.class);
            Model model = entity.getComponent(Model.class);

            this.geometryShaderProgram.setModelMatrix(transformable);

            for (int i = 0; i < model.getMeshes().length; i++) {
                Material material = model.getMaterial()[i];
                Mesh mesh = model.getMeshes()[i];

                this.geometryShaderProgram.setMaterial(material);

                MeshRenderer.bind(mesh, material);
                MeshRenderer.render(mesh);
                MeshRenderer.unbind(mesh, material);
            }
        }

//        if (scene.isWireframe())
//            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

}
