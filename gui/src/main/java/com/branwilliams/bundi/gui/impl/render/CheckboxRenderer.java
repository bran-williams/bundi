package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.gui.ShapeRenderer;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.components.Checkbox;
import com.branwilliams.bundi.gui.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.*;

import static com.branwilliams.bundi.engine.util.ColorUtils.getColorWithEffects;
import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_DEFAULT;
import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_HIGHLIGHT;

/**
 * Basic checkbox renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class CheckboxRenderer extends AbstractComponentRenderer<Checkbox> {

    public CheckboxRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Checkbox checkbox) {
        Color checkboxColor = ColorUtils.getColorWithEffects(toolbox.get(COLOR_DEFAULT), checkbox.isHovered(), checkbox.isPressed());
        shapeRenderer.drawRect(checkbox.getCheckbox(), checkboxColor);

        if (checkbox.isEnabled()) {
            Color checkedColor = getColorWithEffects(checkbox.isEnabled() ? toolbox.get(COLOR_HIGHLIGHT) : toolbox.get(COLOR_DEFAULT), checkbox.isHovered(), checkbox.isPressed());
            shapeRenderer.drawRect(new int[]{checkbox.getX() + 2, checkbox.getY() + 2, 8, 8}, checkedColor);
        }
        /*GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.get(COLOR_DEFAULT), checkbox.isHovered(), Mouse.isButtonDown(0)));
        GLUtils.drawRect(checkbox.getCheckbox());
        if (checkbox.isEnabled()) {
            GLUtils.glColor(GLUtils.getColorWithEffects(checkbox.isEnabled() ? toolbox.get(COLOR_HIGHLIGHT) : toolbox.get(COLOR_DEFAULT), checkbox.isHovered(), Mouse.isButtonDown(0)));
            GLUtils.drawRect(new int[] { checkbox.getX() + 2, checkbox.getY() + 2, 8, 8 });
        }*/
        fontRenderer.drawString(checkbox.getFont(), checkbox.getText(), checkbox.getX() + 2 + Checkbox.CHECKBOX_SIZE, checkbox.getY(), 0xFFFFFFFF);
    }
}
