package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.cloth.pipeline.ClothRenderPass;
import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;

/**
 * From https://viscomp.alexandra.dk/?p=147
 *
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothScene extends AbstractScene {

    private Camera camera;

    private Cloth cloth;

    public ClothScene() {
        super("cloth");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        cloth = new Cloth( new ClothPhysicsParameters(0.01F, 0.5F * 0.5F, 15), 1024, 1024);
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new ClothRenderPass(this::getCloth));
        ClothRenderer renderer = new ClothRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {

    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);
        cloth.update();
    }

    public Cloth getCloth() {
        return cloth;
    }

    public Camera getCamera() {
        return camera;
    }
}
