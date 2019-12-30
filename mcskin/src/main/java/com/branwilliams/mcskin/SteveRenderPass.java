package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.mcskin.steve.MCModel;
import com.branwilliams.mcskin.steve.ModelPart;
import org.joml.Matrix4f;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class SteveRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<MCModel> mcModel;

    private DynamicShaderProgram dynamicShaderProgram;

    private Transformable tempTransform = new Transformation();

    private Matrix4f tempMatrix = new Matrix4f();

    private Transformable baseTransform = new Transformation().position(0F, 0F, 0F).rotate(180, 0, 0).scale(0.25F);

    private Matrix4f modelMatrix = new Matrix4f();


    public SteveRenderPass(Supplier<Camera> camera, Supplier<MCModel> mcModel) {
        this.camera = camera;
        this.mcModel = mcModel;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            dynamicShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV_NORMAL, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_CULL_FACE);
        float scale = 1F;
        MCModel mcModel = this.mcModel.get();
        if (mcModel != null && mcModel.hasTexture()) {
            modelMatrix = Mathf.toModelMatrix(modelMatrix, baseTransform);

            dynamicShaderProgram.bind();
            dynamicShaderProgram.setProjectionMatrix(renderContext.getProjection());
            dynamicShaderProgram.setViewMatrix(camera.get());

            for (ModelPart modelPart : mcModel.getModelParts()) {
                renderPart(modelPart, scale);
            }
        }
        glEnable(GL_CULL_FACE);
    }

    private void renderPart(ModelPart modelPart, float scale) {
        Mathf.toModelMatrix(tempMatrix, tempTransform.position(modelPart.x, modelPart.y, modelPart.z));

        dynamicShaderProgram.setModelMatrix(modelMatrix.mul(tempMatrix, tempMatrix));
        modelPart.render(scale);
    }

    @Override
    public void destroy() {
        dynamicShaderProgram.destroy();
    }
}
