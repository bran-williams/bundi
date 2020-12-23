package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;

/**
 * Created by Brandon Williams on 6/26/2018.
 */
public class Sprite implements Destructible {

    private final SpriteSheet spriteSheet;

    private final DynamicVAO dynamicVao;

    private final int index;

    private final float width, height;

    private boolean centered;

    private final AABB2f boundingBox;

    public Sprite(SpriteSheet spriteSheet, DynamicVAO dynamicVao, int index, float width, float height,
                  boolean centered) {
        this.spriteSheet = spriteSheet;
        this.dynamicVao = dynamicVao;
        this.index = index;
        this.width = width;
        this.height = height;
        this.centered = centered;
        if (centered) {
            this.boundingBox = new AABB2f(-width/2, -height/2, width/2, height/2);
        } else {
            this.boundingBox = new AABB2f(0, 0, width, height);
        }
    }

    /**
     * Draws this sprite object.
     * */
    public void draw() {
        this.spriteSheet.getTexture().bind();
        this.dynamicVao.draw();
    }

    /**
     * Destroys the vao created for this sprite object.
     * */
    @Override
    public void destroy() {
        this.dynamicVao.destroy();
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public DynamicVAO getDynamicVao() {
        return dynamicVao;
    }

    public boolean hasIndex() {
        return index < 0;
    }

    public int getIndex() {
        return index;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isCentered() {
        return centered;
    }

    public AABB2f getAABB() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return "Sprite{" +
                "index=" + index +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
