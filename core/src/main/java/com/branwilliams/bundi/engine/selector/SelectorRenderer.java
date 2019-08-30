package com.branwilliams.bundi.engine.selector;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 2/3/2018.
 */
public class SelectorRenderer extends AbstractRenderer {

    public SelectorRenderer(SelectorScene selectorScene, Window window) {
        super("SelectorRenderer", selectorScene, new RenderPipeline<>(new RenderContext(new Projection(window))));
        //renderPipeline.addLast(new LogoRenderPass());
        renderPipeline.addLast(new SelectorRenderPass(selectorScene));
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        glClearColor(0.1F, 0.2F, 0.7F, 1F);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        super.init(engine, window);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.render(engine, window, deltaTime);
    }
}
