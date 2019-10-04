package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.ShapeRenderer;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.ComboBox;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.Color;

import static com.branwilliams.bundi.gui.impl.Pointers.*;

/**
 * Basic button renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class ComboBoxRenderer extends AbstractComponentRenderer<ComboBox> {

    public ComboBoxRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(ComboBox comboBox) {
        // Highlight this combo box only if it is expanded or hovered.
        boolean highlight = comboBox.isExpanded() || comboBox.isHovered();

        //GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.get(COLOR_DEFAULT), highlight, !comboBox.isExpanded() && comboBox.isHovered() && Mouse.isButtonDown(0)));
        //GLUtils.drawRect(comboBox.getArea());
        fontRenderer.drawString(comboBox.getFont(), comboBox.getSelectedItem().toString(), comboBox.getX() + 3, comboBox.getY() + 1, toolbox.<Color>get(COLOR_ENABLED_TEXT).getRGB());

        //GLUtils.glColor(toolbox.get(highlight ? COLOR_ENABLED_TEXT : COLOR_DISABLED_TEXT));
        imageRenderer.draw(toolbox.get(IMAGE_ARROW), comboBox.getX() + comboBox.getWidth() - 10, comboBox.getY() + 4, 0.5F);

        if (comboBox.isExpanded()) {
            for (int i = 0; i < comboBox.getItems().length; i++) {
                int[] area = comboBox.getItemArea(i);
                boolean hovered = comboBox.getHoveredItem(toolbox.getMouseX(), toolbox.getMouseY()) == i;
                //int color = GLUtils.getColorWithEffects((comboBox.getSelected() == i ? toolbox.get(COLOR_ENABLED_TEXT) : toolbox.get(COLOR_DISABLED_TEXT)), comboBox.isHovered() && hovered, Mouse.isButtonDown(0)).getRGB();

                //GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.get(COLOR_DEFAULT), comboBox.isHovered() && hovered, Mouse.isButtonDown(0)));
                //GLUtils.drawRect(area);

                //drawString(comboBox.getFont(), comboBox.getItems()[i].toString(), area[0], area[1] + area[3] / 2, color);
            }
        }
    }

    /**
     * Originally draws a centered string with the default font renderer.
     * */
    public void drawString(FontData fontData, String text, int x, int y, int color) {
        fontRenderer.drawString(fontData, text, x + 4, y - fontData.getFontHeight() / 2, color);
    }
}
