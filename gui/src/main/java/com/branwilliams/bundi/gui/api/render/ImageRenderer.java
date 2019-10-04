package com.branwilliams.bundi.gui.api.render;

import com.branwilliams.bundi.engine.texture.Texture;

/**
 * Re
 * Created by Brandon Williams on 3/7/2017.
 */
public interface ImageRenderer {

    /**
     * Draws the given image at the x and y positions with the given width and height.
     * */
    void draw(Texture texture, int x, int y, int width, int height);

    /**
     * Draws the given image at the x and y coordinates scaled by the scale factor provided.
     * */
    void draw(Texture texture, int x, int y, float scale);

}
