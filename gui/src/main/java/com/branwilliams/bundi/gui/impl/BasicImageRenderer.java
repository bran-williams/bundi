package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;

/**
 * Basic image renderer. <br/>
 * Created by Brandon Williams on 3/15/2017.
 */
public class BasicImageRenderer implements ImageRenderer {

    @Override
    public void draw(Texture texture, int x, int y, int width, int height) {
        texture.bind();
        //GLUtils.drawTextureRect(x, y, width, height, 0F, 0F, 1F, 1F);
        texture.unbind();
    }

    @Override
    public void draw(Texture texture, int x, int y, float scale) {
        texture.bind();
        //GLUtils.drawTextureRect(x, y, image.getWidth() * scale, image.getHeight() * scale, 0F, 0F, 1F, 1F);
        texture.unbind();
    }
}
