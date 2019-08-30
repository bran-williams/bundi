package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;

/**
 * Created by Brandon Williams on 7/1/2018.
 */
public abstract class AbstractRenderer implements Renderer, Window.WindowListener {

    private final String name;

    protected RenderPipeline renderPipeline;

    public AbstractRenderer(String name, Scene scene, RenderPipeline renderPipeline) {
        this.name = name;
        this.renderPipeline = renderPipeline;
        scene.addWindowListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        renderPipeline.init(engine, window);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        renderPipeline.render(engine, window);
    }

    @Override
    public void destroy() {
        renderPipeline.destroy();
    }

    @Override
    public void resize(Window window, int width, int height) {
        renderPipeline.resize(window, width, height);
    }

    public RenderPipeline getRenderPipeline() {
        return renderPipeline;
    }

    public void setRenderPipeline(RenderPipeline renderPipeline) {
        this.renderPipeline = renderPipeline;
    }

    @Override
    public String getName() {
        return name;
    }
}
