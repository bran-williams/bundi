package com.branwilliams.bundi.engine.selector;

import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelRenderer;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.model.ModelLoader;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since July 07, 2019
 */
public class LogoRenderPass extends RenderPass<RenderContext> {

    private final Transformable logoTransformable = new Transformation().position(0F, 0F, -3F);

    private DynamicShaderProgram shaderProgram;

    private Projection projection;

    private Camera camera;

    private Model logoModel;

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        camera = new Camera();

        projection = new Projection(window, 60, 0.001f, 1000f);

        try {
            TextureLoader textureLoader = new TextureLoader(engine.getContext());
            ModelLoader modelLoader = new ModelLoader(engine.getContext(), textureLoader);
            logoModel = modelLoader.load("models/logo2/logo2.obj", "models/logo2/",
                    VertexFormat.POSITION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glEnable(GL_DEPTH_TEST);

        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(projection);
        shaderProgram.setViewMatrix(camera);
        shaderProgram.setColor(new Vector4f(0F, 0.5F, 1F, 1F));

        logoTransformable.getRotation().y += 0.2F;
        shaderProgram.setModelMatrix(logoTransformable);

        ModelRenderer.renderModel(logoModel);

        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        this.shaderProgram.destroy();
        this.logoModel.destroy();
    }
}
