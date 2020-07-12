package com.branwilliams.demo.template2d.parallax;

import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.texture.Texture;

public class ParallaxObject {

    public final Texture texture;

    private float offsetX;

    private float offsetY;

    private ParallaxMovementType movementType;

    private ParallaxDrawType drawType;

    private ParallaxSizeType sizeType;

    private float scale;

    public ParallaxObject(Texture texture, ParallaxMovementType movementType,
                          ParallaxDrawType drawType, ParallaxSizeType sizeType, float scale) {
        this.texture = texture;
        this.movementType = movementType;
        this.drawType = drawType;
        this.sizeType = sizeType;
        this.scale = scale;
    }

    public ParallaxMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(ParallaxMovementType movementType) {
        this.movementType = movementType;
    }

    public ParallaxDrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(ParallaxDrawType drawType) {
        this.drawType = drawType;
    }

    public ParallaxSizeType getSizeType() {
        return sizeType;
    }

    public void setSizeType(ParallaxSizeType sizeType) {
        this.sizeType = sizeType;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

}
