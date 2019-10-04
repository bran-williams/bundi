package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.atmosphere.pipeline.passes.AtmosphereRenderPass;
import com.branwilliams.bundi.atmosphere.pipeline.passes.AtmosphereRenderPass2;
import com.branwilliams.bundi.atmosphere.pipeline.passes.ModelRenderPass;
import com.branwilliams.bundi.atmosphere.pipeline.passes.SkydomeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.model.ModelLoader;
import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import org.joml.Vector4f;

import java.io.IOException;

/**
 * Created by Brandon Williams on 9/15/2019.
 */
public class AtmosphereScene extends AbstractScene {

    private Camera camera;

    private Skydome skydome;

    private Material skydomeMaterial;

    public AtmosphereScene() {
        super("atmosphere");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
//        renderPipeline.addLast(new EnableWireframeRenderPass(() -> true));
//        renderPipeline.addLast(new SkydomeRenderPass(this::getCamera, this::getSkydome));
        renderPipeline.addLast(new AtmosphereRenderPass2(this::getSkydomeMaterial, this::getCamera, this::getSkydome));
//        renderPipeline.addLast(new DisableWireframeRenderPass(() -> true));
        AtmosphereRenderer renderer = new AtmosphereRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.lookAt(0, 0, -2);

        SphereMesh skydomeSphere = new SphereMesh(250, 50, 50, VertexFormat.POSITION_NORMAL, true);
        Vector4f apexColor = new Vector4f(0F, 0F, 0.2F, 1F);
        Vector4f centerColor = new Vector4f(0.39F, 0.52F, 0.93F, 1F).mul(0.9F);
        skydome = new Skydome(skydomeSphere, apexColor, centerColor);

        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        try {
            skydomeMaterial = new Material();
            skydomeMaterial.setTexture(0, new Texture(textureLoader.loadTexture("textures/atmosphere/tint.png"), false));
            skydomeMaterial.setTexture(1, new Texture(textureLoader.loadTexture("textures/atmosphere/tint2.png"), false));
            skydomeMaterial.setTexture(2, new Texture(textureLoader.loadTexture("textures/atmosphere/sun.png"), false));
            skydomeMaterial.setTexture(3, new Texture(textureLoader.loadTexture("textures/atmosphere/moon.png"), false));

            System.out.println(skydomeMaterial);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    public Camera getCamera() {
        return camera;
    }

    public Skydome getSkydome() {
        return skydome;
    }

    public Material getSkydomeMaterial() {
        return skydomeMaterial;
    }
}
