package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.containers.ScrollableContainer;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;


/**
 * Basic scrollable container renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class ScrollableContainerRenderer extends AbstractComponentRenderer<ScrollableContainer> {

    public ScrollableContainerRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void pre(ScrollableContainer container) {
        /*glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glStencilMask(0xFF);
        glColorMask(false, false, false, false);
        glDepthMask(false);
        RenderUtils.drawRect(container.getRenderArea());
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glStencilFunc(GL_EQUAL, 1, 0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        //glColorMask(false, false, false, false);
        //glDepthMask(false);*/
        //glEnable(GL_SCISSOR_TEST);
        //GLUtils.glScissor(container.getArea());
    }

    @Override
    public void render(ScrollableContainer container) {
        if (container.getOpacity() > 0F) {
            shapeRenderer.drawRect(container.getRenderArea().getArea(), container.getBackgroundColor());
        }
        if (container.getVerticalScrollbar().has()) {
            shapeRenderer.drawRect(container.getVerticalScrollbar().getArea(), container.getScrollbarBackgroundColor());
            shapeRenderer.drawRect(container.getVerticalScrollbar().getScrollbar(), ColorUtils.getColorWithEffects(container.getScrollbarColor(), container.getVerticalScrollbar().isScrolling() || (container.isHovered() && container.getVerticalScrollbar().isPointInsideBar(toolbox.getMouseX(), toolbox.getMouseY())), false));
        }
        if (container.getHorizontalScrollbar().has()) {
            shapeRenderer.drawRect(container.getHorizontalScrollbar().getArea(), container.getScrollbarBackgroundColor());
            shapeRenderer.drawRect(container.getHorizontalScrollbar().getScrollbar(), ColorUtils.getColorWithEffects(container.getScrollbarColor(), container.getHorizontalScrollbar().isScrolling() || (container.isHovered() && container.getHorizontalScrollbar().isPointInsideBar(toolbox.getMouseX(), toolbox.getMouseY())), false));
        }
    }

    @Override
    public void post(ScrollableContainer container) {
        //glDisable(GL_SCISSOR_TEST);
        //glDisable(GL_STENCIL_TEST);
    }
}
