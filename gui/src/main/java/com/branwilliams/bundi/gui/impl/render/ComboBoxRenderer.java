package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.ComboBox;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.Color;

import static com.branwilliams.bundi.engine.util.ColorUtils.getColorWithEffects;
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
        boolean mouseDown = toolbox.getWindow().isMouseButtonPressed(0);

        // Highlight this combo box only if it is expanded or hovered.
        boolean highlight = comboBox.isExpanded() || comboBox.isHovered();

        Color comboBoxColor = getColorWithEffects(toolbox.get(COLOR_DEFAULT), highlight, !comboBox.isExpanded() && comboBox.isHovered() && mouseDown);
        shapeRenderer.drawRect(comboBox.getArea(), comboBoxColor);
        fontRenderer.drawString(comboBox.getFont(), comboBox.getSelectedItem().toString(), comboBox.getX() + 3, comboBox.getY() + 1, toolbox.<Color>get(COLOR_ENABLED_TEXT).getRGB());

        Color arrowColor = toolbox.get(highlight ? COLOR_ENABLED_TEXT : COLOR_DISABLED_TEXT);
        int arrowSize = (int) (comboBox.getItemHeight() * 0.5F);
        int padding = (int) (comboBox.getItemHeight() * 0.25F);
        shapeRenderer.drawTriangle(comboBox.getX() + comboBox.getWidth() - arrowSize - padding, comboBox.getY() + padding, comboBox.getX() + comboBox.getWidth() - padding, comboBox.getY() + padding + arrowSize, arrowColor.getRGB());

        if (comboBox.isExpanded()) {
            for (int i = 0; i < comboBox.getItems().length; i++) {
                int[] area = comboBox.getItemArea(i);
                boolean hovered = comboBox.getHoveredItem(toolbox.getMouseX(), toolbox.getMouseY()) == i;

                Color backgroundColor = getColorWithEffects(toolbox.get(COLOR_DEFAULT), comboBox.isHovered() && hovered, mouseDown);
                shapeRenderer.drawRect(area, backgroundColor);

                Color textColor = getColorWithEffects((comboBox.getSelected() == i ? toolbox.get(COLOR_ENABLED_TEXT) : toolbox.get(COLOR_DISABLED_TEXT)), comboBox.isHovered() && hovered, mouseDown);
                drawString(comboBox.getFont(), comboBox.getItems()[i].toString(), area[0], area[1] + area[3] / 2, textColor);
            }
        }
    }

    /**
     * Originally draws a centered string with the default font renderer.
     * */
    public void drawString(FontData fontData, String text, int x, int y, Color color) {
        fontRenderer.drawString(fontData, text, x + 4, y - fontData.getFontHeight() / 2, color.getRGB());
    }
}
