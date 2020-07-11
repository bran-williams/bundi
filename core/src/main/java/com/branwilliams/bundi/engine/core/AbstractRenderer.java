package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;

/**
 * Created by Brandon Williams on 7/1/2018.
 */
public abstract class AbstractRenderer <Context extends RenderContext> implements Renderer, WindowListener {

    private final String name;

    protected RenderPipeline<Context> renderPipeline;

    public AbstractRenderer(String name, Scene scene, RenderPipeline<Context> renderPipeline) {
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
        renderPipeline.render(engine, window, deltaTime);
    }

    @Override
    public void destroy() {
        renderPipeline.destroy();
    }

    @Override
    public void resize(Window window, int width, int height) {
        renderPipeline.resize(window, width, height);
    }

    public RenderPipeline<Context> getRenderPipeline() {
        return renderPipeline;
    }

    public void setRenderPipeline(RenderPipeline<Context> renderPipeline) {
        this.renderPipeline = renderPipeline;
    }

    @Override
    public String getName() {
        return name;
    }
}
