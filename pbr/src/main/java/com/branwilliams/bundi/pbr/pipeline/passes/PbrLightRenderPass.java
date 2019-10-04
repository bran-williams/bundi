package com.branwilliams.bundi.pbr.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.pbr.pipeline.shaders.PbrLightShaderProgram;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;

/**
 * Created by Brandon Williams on 9/27/2018.
 */
public class PbrLightRenderPass extends RenderPass<PbrRenderContext> {

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final IComponentMatcher componentMatcher;

    private PbrLightShaderProgram lightShaderProgram;

    public PbrLightRenderPass(Scene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
        this.componentMatcher = scene.getEs().matcher(PointLight.class);
    }

    @Override
    public void init(PbrRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            lightShaderProgram = new PbrLightShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create light shader program!");
            throw new InitializationException(e);
        }
    }

    /**
     * 1. Binds the scene buffer and clears it.
     * 2. Disables depth testing, enables blending, and sets the blend mode to additive and one-to-one.
     *    i.e. pixel = ( 1 * src + 1 * dst)
     * 3. Binds the light shader program and sets it's uniform variables.
     * 4. Binds the G-Buffer textures needed by the light shader program.
     * 5. Binds a single quad mesh for render passes.
     * 6. Render lights.
     * 7. Unbinds the mesh, gbuffer textures, shader program, and scene buffer.
     * */
    @Override
    public void render(PbrRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        renderContext.getSceneBuffer().bind();
        // Only clear the color buffer because the depth from the geometry pass is needed.
        glClear(GL_COLOR_BUFFER_BIT);

        //glEnable(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);
        // Do not write to the depth buffer.
        glDepthMask(false);

        // Additive blending.
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        this.lightShaderProgram.bind();
        this.lightShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.lightShaderProgram.setViewMatrix(camera.get());

        renderContext.bindGbuffer();

        MeshRenderer.bind(renderContext.getRenderPassMesh(), null);

        List<PointLight[]> lights = getLights(PbrLightShaderProgram.MAX_LIGHTS_PER_PASS);
        for (PointLight[] lights_ : lights) {
            try {
                lightShaderProgram.setLights(lights_);
            } catch (ShaderUniformException e) {
                e.printStackTrace();
            }
            MeshRenderer.render(renderContext.getRenderPassMesh());
        }
        MeshRenderer.unbind(renderContext.getRenderPassMesh(), null);

        renderContext.unbindGbuffer();
    }

    private List<PointLight[]> getLights(int maxSize) {
        List<PointLight[]> lights = new ArrayList<>();

        PointLight[] temp = new PointLight[maxSize];
        int i = 0;
        for (IEntity entity : scene.getEs().getEntities(componentMatcher)) {
            PointLight light = entity.getComponent(PointLight.class);
            temp[i] = light;
            i++;

            // Add array to list and reset temp.
            if (i >= maxSize) {
                i = 0;
                lights.add(temp);
                temp = new PointLight[maxSize];
            }
        }
        // If the first element is not null, then the temp must have some lights within it still.
        if (temp[0] != null) {
            lights.add(Arrays.copyOf(temp, i));
        }
        return lights;
    }
}
