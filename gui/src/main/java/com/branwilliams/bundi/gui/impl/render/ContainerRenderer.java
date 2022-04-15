package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import static com.branwilliams.bundi.engine.util.ColorUtils.toARGB;
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
        // TODO use opacity
        if (container.getOpacity() > 0F) {
            shapeRenderer.drawRect(container.getArea(), toARGB(container.getBackgroundColor().getRGB(),
                    container.getOpacity()));
        }
    }
}
