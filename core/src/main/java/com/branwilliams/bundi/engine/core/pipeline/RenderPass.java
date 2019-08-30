package com.branwilliams.bundi.engine.core.pipeline;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;

/**
 * Created by Brandon Williams on 9/19/2018.
 */
public abstract class RenderPass <CurrentContext extends RenderContext> implements Destructible {

    protected RenderPass<CurrentContext> next;

    /**
     * Initialize this render pass.
     * */
    public abstract void init(CurrentContext renderContext, Engine engine, Window window) throws InitializationException;

    /**
     * Performs rendering of things.
     * */
    public abstract void render(CurrentContext renderContext, Engine engine, Window window);

    /**
     * Invoked if this render pass is the last one within the pipeline.
     * */
    public void finish(CurrentContext renderContext) {

    }

    /**
     * Invoked when this render pass is destroyed and no longer used.
     * */
    @Override
    public void destroy() {

    }

    /**
     *
     * */
    //public abstract void destroy();

    public RenderPass<CurrentContext> getNextPass() {
        return next;
    }

    public void setNextPass(RenderPass<CurrentContext> nextRenderPass) {
        this.next = nextRenderPass;
    }
}
