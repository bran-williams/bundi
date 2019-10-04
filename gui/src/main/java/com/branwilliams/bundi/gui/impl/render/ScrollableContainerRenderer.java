package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.ShapeRenderer;
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
        /*if (!container.getTag().equals("invisible-background")) {
            GLUtils.glColor(toolbox.get(COLOR_BACKGROUND));
            GLUtils.drawRect(container.getRenderArea());
        }
        if (container.getVerticalScrollbar().has()) {
            GLUtils.glColor(toolbox.get(COLOR_DEFAULT));
            GLUtils.drawRect(container.getVerticalScrollbar().getArea());
            GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.<Color>get(COLOR_DEFAULT).brighter(), container.getVerticalScrollbar().isScrolling() || (container.isHovered() && container.getVerticalScrollbar().isPointInsideBar(toolbox.getMouseX(), toolbox.getMouseY())), false));
            GLUtils.drawRect(container.getVerticalScrollbar().getScrollbar());
        }
        if (container.getHorizontalScrollbar().has()) {
            GLUtils.glColor(toolbox.get(COLOR_DEFAULT));
            GLUtils.drawRect(container.getHorizontalScrollbar().getArea());
            GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.<Color>get(COLOR_DEFAULT).brighter(), container.getHorizontalScrollbar().isScrolling() || (container.isHovered() && container.getHorizontalScrollbar().isPointInsideBar(toolbox.getMouseX(), toolbox.getMouseY())), false));
            GLUtils.drawRect(container.getHorizontalScrollbar().getScrollbar());
        }*/
    }

    @Override
    public void post(ScrollableContainer container) {
        //glDisable(GL_SCISSOR_TEST);
        //glDisable(GL_STENCIL_TEST);
    }
}
