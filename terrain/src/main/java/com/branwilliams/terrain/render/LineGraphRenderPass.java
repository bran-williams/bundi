package com.branwilliams.terrain.render;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.terrain.TerrainTile;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class LineGraphRenderPass extends RenderPass<RenderContext> {

    private final Scene scene;

    private Projection orthoProjection;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    private Transformable transformable = new Transformation();

    public LineGraphRenderPass(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_2D_COLOR);
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create terrain shader program!");
            throw new InitializationException(e);
        }
        dynamicVAO = new DynamicVAO(VertexFormat.POSITION_2D_COLOR);
        orthoProjection = new Projection(window);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        this.shaderProgram.bind();
        this.shaderProgram.setProjectionMatrix(orthoProjection);

        float viewingScale = 64F;

        transformable.position(10, window.getHeight() * 0.5F, 0F);
        this.shaderProgram.setModelMatrix(transformable);

        float startX = 0;
        float finalX = 12F;
        float stepSize = 0.001F;
        drawLine(x -> 0F, startX, finalX, finalX, 0F, 0F, viewingScale, viewingScale, 0xFFFFFFFF);

        drawLine(x -> expImpulse(0.125F, x), startX, finalX, stepSize, 0F, 0F, viewingScale, viewingScale, 0xFFFF0000); // red
        drawLine(x -> expImpulse(0.5F,   x), startX, finalX, stepSize, 0F, 0F, viewingScale, viewingScale, 0xFF00FF00); // green
        drawLine(x -> expImpulse(1F,     x), startX, finalX, stepSize, 0F, 0F, viewingScale, viewingScale, 0xFF0000FF); // blue
        drawLine(x -> expImpulse(2F,     x), startX, finalX, stepSize, 0F, 0F, viewingScale, viewingScale, 0xFFFF00FF); // pink
    }

    private void drawLine(Function<Float, Float> func, float startX, float finalX, float stepSize, float originX, float originY, float xscale, float yscale, int color) {
        float x = startX;
        float y = func.apply(x);

        dynamicVAO.begin();

        // draw the first point
        dynamicVAO.position(originX + x * xscale, originY - y * yscale).color(color).endVertex();

        // every other point
        for (; x < finalX; x += stepSize) {
            y = func.apply(x);
            dynamicVAO.position(originX + x * xscale, originY - y * yscale).color(color).endVertex();
        }

        // draw the final point..
        if (x + stepSize >= finalX) {
            y = func.apply(finalX);
            dynamicVAO.position(originX + x * xscale, originY - y * yscale).color(color).endVertex();
        }

        dynamicVAO.draw(GL_LINE_STRIP);
    }

    private float expImpulse( float k, float x ) {
        float h = k * x;
        return h * (float) Math.exp(1.0 - h);
    }
}
