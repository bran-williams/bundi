package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

/**
 * Basic label renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class LabelRenderer extends AbstractComponentRenderer<Label> {

    private static final float ALIGNMENT_PADDING = 2;

    public LabelRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Label label) {
        float x = 0;

        switch (label.getAlignment()) {
            case LEFT:
                x = label.getX();
                break;
            case RIGHT:
                x = label.getX() + label.getWidth() - label.getTextWidth() - ALIGNMENT_PADDING;
                break;
            case CENTER:
                x = label.getX() + (label.getWidth() * 0.5F) - (label.getTextWidth() * 0.5F);
                break;
            default:
                System.out.println("wtf");
        }

        fontRenderer.drawString(label.getFont(), label.getText(), (int) x, label.getY(), label.getColor().getRGB());
    }
}
