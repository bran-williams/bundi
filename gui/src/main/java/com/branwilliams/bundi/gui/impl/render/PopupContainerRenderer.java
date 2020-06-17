package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.PopupContainer;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import javax.swing.*;


/**
 * Basic container renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class PopupContainerRenderer extends AbstractComponentRenderer<PopupContainer> {

    public PopupContainerRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(PopupContainer container) {
        // TODO use opacity
        if (container.getOpacity() > 0F) {
            shapeRenderer.drawRect(container.getArea(), container.getBackgroundColor());
        }
    }
}
