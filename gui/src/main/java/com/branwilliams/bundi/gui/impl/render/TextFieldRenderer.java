package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.ShapeRenderer;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.components.TextField;
import com.branwilliams.bundi.gui.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.*;

import static com.branwilliams.bundi.gui.impl.Pointers.*;

/**
 * Basic textfield renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class TextFieldRenderer extends AbstractComponentRenderer<TextField> {

    public TextFieldRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(TextField textField) {
        /*if (textField.getTag().equals("lined")) {
            GLUtils.glColor(GLUtils.getColorWithEffects(textField.isTyping() ? toolbox.get(COLOR_HIGHLIGHT) : toolbox.<Color>get(COLOR_DEFAULT).brighter(), textField.isHovered(), Mouse.isButtonDown(0)));
            GLUtils.drawLine(1F, textField.getX(), textField.getY() + textField.getHeight() - 1F, textField.getX() + textField.getWidth(), textField.getY() + textField.getHeight() - 1F);
        } else {
            GLUtils.glColor(GLUtils.getColorWithEffects(textField.isTyping() ? toolbox.<Color>get(COLOR_DEFAULT).brighter() : toolbox.get(COLOR_DEFAULT), textField.isHovered(), Mouse.isButtonDown(0)));
            GLUtils.drawRect(textField.getArea());
        }*/
        if (textField.hasText()) {
            int color = textField.isTyping() ? textField.getColor().getRGB() : textField.getColor().darker().getRGB();
            drawString(textField.getFont(), textField.getRenderText(textField.isTyping()), textField.getX(), textField.getY() + textField.getHeight() / 2, color);
        } else {
            drawString(textField.getFont(), textField.getDefaultText(), textField.getX(), textField.getY() + textField.getHeight() / 2, toolbox.<Color>get(COLOR_DISABLED_TEXT).getRGB());
        }
    }

    /**
     * Originally draws a centered string with the default font renderer.
     * */
    public void drawString(FontData fontData, String text, int x, int y, int color) {
        fontRenderer.drawString(fontData, text, x + 2, y - fontData.getFontHeight() / 2, color);
    }
}
