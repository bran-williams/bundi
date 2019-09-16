package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.atmosphere.pipeline.passes.SkydomeRenderPass;
import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import org.joml.Vector4f;

/**
 * Created by Brandon Williams on 9/15/2019.
 */
public class AtmosphereScene extends AbstractScene {

    private Camera camera;

    private Skydome skydome;

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
        renderPipeline.addLast(new SkydomeRenderPass(this::getCamera, this::getSkydome));
        AtmosphereRenderer renderer = new AtmosphereRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.lookAt(0, 0, -2);

        SphereMesh skydomeSphere = new SphereMesh(250, 60, 60, false, true);
        Vector4f apexColor = new Vector4f(0F, 0F, 0.2F, 1F);
        Vector4f centerColor = new Vector4f(0.39F, 0.52F, 0.93F, 1F);
        skydome = new Skydome(skydomeSphere, apexColor, centerColor);
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
}
