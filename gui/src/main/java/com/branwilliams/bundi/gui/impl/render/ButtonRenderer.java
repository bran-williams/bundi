package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.*;

import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_DEFAULT_BUTTON_ACTIVE;
import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_DEFAULT_BUTTON_INACTIVE;


/**
 * Basic container renderer. <br/>
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
    }
}
