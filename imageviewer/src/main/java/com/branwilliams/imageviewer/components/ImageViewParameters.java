package com.branwilliams.imageviewer.components;

import com.branwilliams.bundi.engine.shader.Transformable;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class ImageViewParameters {

    private Transformable transform;

    public ImageViewParameters(Transformable transform) {
        this.transform = transform;
    }

    public Transformable getTransform() {
        return transform;
    }
}
