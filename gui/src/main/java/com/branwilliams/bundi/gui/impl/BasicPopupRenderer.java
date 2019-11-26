package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.impl.render.ShapeRenderer;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.api.render.PopupRenderer;
import java.awt.Color;

import static com.branwilliams.bundi.gui.impl.Pointers.FONT_TOOLTIP;

/**
 * Basic popup renderer. <br/>
 * Created by Brandon Williams on 2/24/2017.
 */
public class BasicPopupRenderer implements PopupRenderer {

    private final ShapeRenderer shapeRenderer;

    private final Toolbox toolbox;

    private final FontRenderer fontRenderer;

    private final ImageRenderer imageRenderer;

    public BasicPopupRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.toolbox = toolbox;
        this.fontRenderer = fontRenderer;
        this.imageRenderer = imageRenderer;
    }

    @Override
    public void drawTooltip(String tooltip, int x, int y) {
        if (tooltip != null) {
            FontData font = toolbox.get(FONT_TOOLTIP);
            shapeRenderer.drawRect(x - 2, y - font.getFontHeight() - 2, x + font.getStringWidth(tooltip) + 2, y, toolbox.<Color>get(Pointers.COLOR_DEFAULT));
            fontRenderer.drawString(font, tooltip, x, y - font.getFontHeight() - 1, 0xFFFFFFFF);

            /*GLUtils.glColor(toolbox.get(Pointers.COLOR_DEFAULT));
            GLUtils.drawRect(x - 2, y - font.getFontHeight() - 2, x + font.getStringWidth(tooltip) + 2, y);
            GLUtils.glColor(toolbox.get(Pointers.COLOR_TERTIARY_BACKGROUND));
            GLUtils.drawBorder(2, x - 2, y - font.getFontHeight() - 2, x + font.getStringWidth(tooltip) + 2, y);
            fontRenderer.drawString(font, tooltip, x, y - font.getFontHeight() - 1, 0xFFFFFFFF);*/
        }
    }
}
