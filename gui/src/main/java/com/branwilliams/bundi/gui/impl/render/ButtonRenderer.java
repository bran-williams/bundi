package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;
import com.branwilliams.bundi.gui.impl.Pointers;

import java.awt.Color;

import static com.branwilliams.bundi.engine.util.ColorUtils.getColorWithEffects;
import static com.branwilliams.bundi.gui.impl.Pointers.*;

/**
 * Basic button renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class ButtonRenderer extends AbstractComponentRenderer<Button> {

    public ButtonRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }
    @Override
    public void render(Button button) {
        Color backgroundColor;
        if (button.getBackgroundColor() != null) {
            backgroundColor = button.getBackgroundColor();
        } else if (button.isActive()) {
            backgroundColor = toolbox.get(COLOR_DEFAULT_BUTTON_ACTIVE);
        } else {
            backgroundColor = toolbox.get(COLOR_DEFAULT_BUTTON_INACTIVE);
        }
        backgroundColor = ColorUtils.getColorWithEffects(backgroundColor, button.isHovered(), button.isPressed());

        shapeRenderer.drawRect(button.getArea(), backgroundColor.getRGB());

        Color textColor;
        if (button.getTextColor() != null) {
            textColor = button.getTextColor();
        } else if (button.isActive()) {
            textColor = toolbox.get(COLOR_DEFAULT_BUTTON_TEXT_ACTIVE);
        } else {
            textColor = toolbox.get(COLOR_DEFAULT_BUTTON_TEXT_INACTIVE);
        }
        int color = ColorUtils.getColorWithEffects(textColor, button.isHovered(), button.isPressed()).getRGB();
//        int color = button.isActive() ? toolbox.<Color>get(Pointers.COLOR_ENABLED_TEXT).getRGB() : toolbox.<Color>get(Pointers.COLOR_DISABLED_TEXT).getRGB();
        drawString(button.getFont(), button.getText(), button.getX(), button.getY() + button.getHeight() / 2, color);
    }

    /**
     * Originally draws a centered string with the default font renderer.
     * */
    public void drawString(FontData fontData, String text, int x, int y, int color) {
        fontRenderer.drawString(fontData, text, x + 4, y - fontData.getFontHeight() / 2, color);
    }
}
