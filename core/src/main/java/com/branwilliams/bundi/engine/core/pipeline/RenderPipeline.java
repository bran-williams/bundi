package com.branwilliams.bundi.engine.core.pipeline;

import com.branwilliams.bundi.engine.Profiler;
import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;

import java.util.function.Predicate;

/**
 * Created by Brandon Williams on 9/15/2018.
 */
public class RenderPipeline <CurrentContext extends RenderContext> implements Window.WindowListener, Destructible {

    private final Profiler profiler = new Profiler("render_profiler");

    private RenderPass<CurrentContext> initialRenderPass;

    protected CurrentContext renderContext;

    public RenderPipeline(CurrentContext renderContext) {
        this.renderContext = renderContext;
    }

    /**
     * Runs the init function for each render pass.
     * */
    public void init(Engine engine, Window window) throws InitializationException {
        renderContext.init(engine, window);

        RenderPass<CurrentContext> current = initialRenderPass;
        while (current != null) {
            current.init(renderContext, engine, window);
            current = current.getNextPass();
        }
    }

    /**
     * Iterates over each render pass and invoke their render functions.
     * */
    public void render(Engine engine, Window window, double deltaTime) {
        RenderPass<CurrentContext> current = initialRenderPass;
        profiler.begin(current.getClass().getSimpleName());
        current.render(renderContext, engine, window, deltaTime);

        while (current.getNextPass() != null) {
            current = current.getNextPass();
            profiler.endBegin(current.getClass().getSimpleName());

            // render this pass.
            current.render(renderContext, engine, window, deltaTime);
        }
        profiler.end();

        // finish with the last render pass.
        current.finish(renderContext);
    }

    /**
     * Adds this render pass to the end of the chain of render passes.
     * */
    public void addLast(RenderPass<CurrentContext> renderPass) {
        if (this.initialRenderPass == null) {
            this.initialRenderPass = renderPass;
        } else {
            RenderPass<CurrentContext> current = initialRenderPass;
            while (current.getNextPass() != null) {
                current = current.getNextPass();
            }
            current.setNextPass(renderPass);
        }
    }

    public void addFirst(RenderPass<CurrentContext> renderPass) {
        if (this.initialRenderPass == null) {
            this.initialRenderPass = renderPass;
        } else {
            RenderPass<CurrentContext> temp = this.initialRenderPass;
            this.initialRenderPass = renderPass;
            renderPass.setNextPass(temp);
        }
    }

    public boolean addBefore(Class<? extends RenderPass<CurrentContext>> beforeClass, RenderPass<CurrentContext> renderPass) {
        return addBefore((temp) -> temp.getClass().equals(beforeClass), renderPass);
    }

    public boolean addBefore(RenderPass<CurrentContext> before, RenderPass<CurrentContext> renderPass) {
        return addBefore(before::equals, renderPass);
    }

        /**
         * Puts the given RenderPass before the RenderPass the predicate accepts.
         * */
    public boolean addBefore(Predicate<RenderPass<CurrentContext>> predicate, RenderPass<CurrentContext> renderPass) {
        if (initialRenderPass != null) {
            RenderPass<CurrentContext> current = initialRenderPass;

            // Swap the initial render pass if necessary
            if (predicate.test(current)) {
                initialRenderPass = renderPass;
                renderPass.setNextPass(current);
                return true;
            }

            while (current != null) {
                RenderPass<CurrentContext> temp = current.getNextPass();
                if (predicate.test(temp)) {
                    current.setNextPass(renderPass);
                    renderPass.setNextPass(temp);
                    return true;
                }
                current = current.getNextPass();
            }
            return true;
        }
        return false;
    }

    public boolean addAfter(Class<? extends RenderPass<CurrentContext>> afterClass, RenderPass<CurrentContext> renderPass) {
        return addAfter((temp) -> temp.getClass().equals(afterClass), renderPass);
    }

    public boolean addAfter(RenderPass<CurrentContext> after, RenderPass<CurrentContext> renderPass) {
        return addAfter(after::equals, renderPass);
    }

        /**
         *
         * */
    public boolean addAfter(Predicate<RenderPass<CurrentContext>> predicate, RenderPass<CurrentContext> renderPass) {
        if (initialRenderPass != null) {
            RenderPass<CurrentContext> current = initialRenderPass;

            while (current != null) {
                if (predicate.test(current)) {
                    RenderPass<CurrentContext> temp = current.next;
                    current.next = renderPass;
                    renderPass.setNextPass(temp);
                    return true;
                }
                current = current.next;
            }
        }
        return false;
    }

    /**
     * Updates the render context with the new window dimensions.
     * */
    @Override
    public void resize(Window window, int width, int height) {
        renderContext.windowResized(window, width, height);
    }

    @Override
    public void destroy() {
        renderContext.destroy();

        RenderPass<CurrentContext> current = initialRenderPass;
        while (current != null) {
            current.destroy();
            current = current.next;
        }

    }

    public CurrentContext getRenderContext() {
        return renderContext;
    }

    public void setRenderContext(CurrentContext renderContext) {
        this.renderContext = renderContext;
    }

    public Profiler getProfiler() {
        return profiler;
    }
}
