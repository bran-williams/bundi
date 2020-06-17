package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.components.Image;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;
import com.branwilliams.bundi.gui.impl.Pointers;

import java.awt.*;

/**
 * Basic button renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class ImageRenderer extends AbstractComponentRenderer<Image> {

    public ImageRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, com.branwilliams.bundi.gui.api.render.ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }
    @Override
    public void render(Image image) {
        if (image.getTexture() != null) {
            shapeRenderer.drawRect(image.getTexture(), image.getArea(), image.getTextureColor().getRGB());
        } else {
            int color = toolbox.<Color>get(Pointers.COLOR_DISABLED_TEXT).getRGB();
            fontRenderer.drawString(image.getAltText(), image.getX(), image.getY(), color);
        }
    }
}
