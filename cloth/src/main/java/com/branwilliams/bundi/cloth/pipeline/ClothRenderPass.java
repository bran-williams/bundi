package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Cloth> cloth;

    public ClothRenderPass(Supplier<Cloth> cloth) {
        this.cloth = cloth;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {

    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {

    }
}
