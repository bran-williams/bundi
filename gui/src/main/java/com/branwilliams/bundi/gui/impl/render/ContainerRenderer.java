package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.ShapeRenderer;
import com.branwilliams.bundi.gui.Container;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_SECONDARY_BACKGROUND;


/**
 * Basic container renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class ContainerRenderer extends AbstractComponentRenderer<Container> {

    public ContainerRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Container container) {
        if (!container.getTag().equals("invisible")) {
            shapeRenderer.drawRect(container.getArea(), toolbox.get(COLOR_SECONDARY_BACKGROUND) );
        }
        /*if (!container.getTag().equals("invisible")) {
            GLUtils.glColor(toolbox.get(COLOR_SECONDARY_BACKGROUND));
            GLUtils.drawRect(container.getArea());
        }*/
    }
}
