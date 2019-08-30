package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.ShapeRenderer;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.components.Label;
import com.branwilliams.bundi.gui.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

/**
 * Basic label renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class LabelRenderer extends AbstractComponentRenderer<Label> {

    public LabelRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Label label) {
        fontRenderer.drawString(label.getFont(), label.getText(), label.getX(), label.getY(), label.getColor().getRGB());
    }
}
