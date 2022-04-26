package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class VoxelCrosshairRenderPass extends RenderPass<VoxelRenderContext> implements WindowListener {

    private static final float CROSSHAIR_SIZE = 8F;

    private final Transformable crosshairTransform = new Transformation();

    private final Vector4f crosshairColor = new Vector4f(1F);

    private final VoxelScene scene;

    private DynamicShaderProgram shapeShaderProgram;

    private DynamicVAO crosshair;

    public VoxelCrosshairRenderPass(VoxelScene scene) {
        scene.addWindowListener(this);
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shapeShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        crosshair = new DynamicVAO(VertexFormat.POSITION);
        crosshairTransform.position(window.getWidth() * 0.5F, window.getHeight() * 0.5F, 0F);
        buildCrosshair();
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);

        if (scene.getGuiScreen() != null) {
            return;
        }

        drawCrosshair(renderContext);
    }

    @Override
    public void resize(Window window, int width, int height) {
        crosshairTransform.position(width * 0.5F, height * 0.5F, 0F);
    }

    private void drawCrosshair(VoxelRenderContext renderContext) {
        shapeShaderProgram.bind();
        shapeShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        shapeShaderProgram.setModelMatrix(crosshairTransform);
        shapeShaderProgram.setColor(crosshairColor);

        crosshair.draw(GL_LINES);
    }

    private void buildCrosshair() {
        crosshair.begin();
        crosshair.position(0, -CROSSHAIR_SIZE, 0).endVertex();
        crosshair.position(0, CROSSHAIR_SIZE, 0).endVertex();

        crosshair.position(-CROSSHAIR_SIZE, 0F, 0).endVertex();
        crosshair.position(CROSSHAIR_SIZE, 0F, 0).endVertex();
        crosshair.compile();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shapeShaderProgram.destroy();
        this.crosshair.destroy();
    }
}
