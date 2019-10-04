package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.ShapeRenderer;
import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.containers.Frame;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.Color;

import static com.branwilliams.bundi.gui.impl.Pointers.*;

public class FrameRenderer extends AbstractComponentRenderer<Frame> {
    public FrameRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Frame frame) {
        Widget titleWidget = frame.getTitleWidget();
        shapeRenderer.drawRect(frame.getArea(), toolbox.get(COLOR_BACKGROUND));
        shapeRenderer.drawRect(titleWidget.getArea(),  toolbox.get(COLOR_SECONDARY_BACKGROUND));
        /*GLUtils.glColor(toolbox.get(COLOR_BACKGROUND));
        GLUtils.drawRect(frame);
        GLUtils.glColor(toolbox.get(COLOR_SECONDARY_BACKGROUND));
        GLUtils.drawRect(titleWidget);
        GLUtils.glColor(toolbox.get(COLOR_TERTIARY_BACKGROUND));
        GLUtils.drawBorder(2F, frame.getX(), frame.getY(), frame.getX() + frame.getWidth(), frame.getY() + frame.getHeight());*/
        fontRenderer.drawString(frame.getFont(), frame.getTitle(), titleWidget.getX() + 4, titleWidget.getY() + titleWidget.getHeight() / 2 - frame.getFont().getFontHeight() / 2, toolbox.<Color>get(COLOR_DISABLED_TEXT).getRGB());
    }
}
