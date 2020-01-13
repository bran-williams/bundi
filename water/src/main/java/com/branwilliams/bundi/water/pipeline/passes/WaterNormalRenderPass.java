package com.branwilliams.bundi.water.pipeline.passes;

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
import com.branwilliams.bundi.water.Water;
import com.branwilliams.bundi.water.pipeline.WaterNormalBuffer;
import com.branwilliams.bundi.water.pipeline.shaders.WaterNormalShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterNormalRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private final IComponentMatcher matcher;

    private WaterNormalShaderProgram shaderProgram;

    private Mesh waterTileMesh;

    public WaterNormalRenderPass(Scene scene) {
        this.scene = scene;
        this.matcher = scene.getEs().matcher(Water.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new WaterNormalShaderProgram(engine.getContext());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        float[] quad = new float[] {
                -1.0F, 1.0F,
                1.0F, 1.0F,
                -1.0F, -1.0F,
                1.0F, -1.0F, };

        waterTileMesh = new Mesh();
        waterTileMesh.bind();
        waterTileMesh.storeAttribute(0, quad, 2);
        waterTileMesh.storeIndices(new int[] { 0, 2, 1, 1, 2, 3 });
        waterTileMesh.unbind();

    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        int width = window.getWidth();
        int height = window.getHeight();

        shaderProgram.bind();

        for (IEntity entity : scene.getEs().getEntities(matcher)) {
            Water water = entity.getComponent(Water.class);
            if (!water.isCopy()) {
                WaterNormalBuffer normalBuffer = water.getNormalBuffer();
                glViewport(0, 0, normalBuffer.getWidth(), normalBuffer.getHeight());
                normalBuffer.bind();
                glClear(GL_COLOR_BUFFER_BIT);

                shaderProgram.setWater(water);

                MeshRenderer.render(waterTileMesh, null);
            }
        }

        ShaderProgram.unbind();

        FrameBufferObject.unbind();
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
