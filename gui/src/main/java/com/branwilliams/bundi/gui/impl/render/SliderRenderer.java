package com.branwilliams.bundi.gui.impl.render;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.ShapeRenderer;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.components.Slider;
import com.branwilliams.bundi.gui.render.ImageRenderer;
import com.branwilliams.bundi.gui.impl.AbstractComponentRenderer;

import java.awt.*;

import static com.branwilliams.bundi.engine.util.ColorUtils.getColorWithEffects;
import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_DEFAULT;
import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_HIGHLIGHT;


/**
 * Basic button renderer. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class SliderRenderer extends AbstractComponentRenderer<Slider> {

    public SliderRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        super(shapeRenderer, toolbox, fontRenderer, imageRenderer);
    }

    @Override
    public void render(Slider slider) {
        shapeRenderer.drawRect(slider.getArea(), toolbox.get(COLOR_DEFAULT));
        Color sliderBarColor = getColorWithEffects(toolbox.get(COLOR_HIGHLIGHT), slider.isSliding() || slider.isHovered(), slider.isSliding());
        shapeRenderer.drawRect(slider.getSliderBar(), sliderBarColor);
        /*GLUtils.glColor(toolbox.get(Pointers.COLOR_DEFAULT));
        GLUtils.drawRect(slider.getArea());
        GLUtils.glColor(GLUtils.getColorWithEffects(toolbox.get(Pointers.COLOR_HIGHLIGHT), slider.isSliding() || slider.isHovered(), Mouse.isButtonDown(0)));
        GLUtils.drawRect(slider.getSliderBar());*/
    }
}
