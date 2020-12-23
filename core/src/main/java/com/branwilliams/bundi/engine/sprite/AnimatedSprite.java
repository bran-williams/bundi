package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.util.RateLimiter;

/**
 * Created by Brandon Williams on 10/8/2018.
 */
public class AnimatedSprite extends Sprite {

    private final RateLimiter rateLimiter;

    private final int[] spriteIndices;

    private int spriteIndex;

    private float xscale;

    private float yscale;

    private boolean rebuild;

    public AnimatedSprite(SpriteSheet spriteSheet, int[] spriteIndices, RateLimiter rateLimiter, float scale) {
        this(spriteSheet, spriteIndices, rateLimiter, scale, scale);
    }

    public AnimatedSprite(SpriteSheet spriteSheet, int[] spriteIndices, RateLimiter rateLimiter,
                          float xscale, float yscale) {
        super(spriteSheet, new DynamicVAO(), -1, spriteSheet.getSpriteWidth() * xscale,
                spriteSheet.getSpriteHeight() * yscale, spriteSheet.isCenteredSprite());
        this.spriteIndices = spriteIndices;
        this.rateLimiter = rateLimiter;
        this.xscale = xscale;
        this.yscale = yscale;

        this.rebuild = true;
    }

    @Override
    public void draw() {
        updateSpriteIndex();
        if (rebuild) {
            SpriteSheet.buildSpriteMesh(getSpriteSheet(), getDynamicVao(), getIndex(), xscale, yscale);
            rebuild = false;
        }
        super.draw();
    }

    @Override
    public int getIndex() {
        updateSpriteIndex();
        return spriteIndices[spriteIndex];
    }

    public void updateSpriteIndex() {
        while (rateLimiter.reached()) {
            this.spriteIndex++;
            if (spriteIndex >= spriteIndices.length) {
                spriteIndex = 0;
            }
            this.rebuild = true;
        }
    }
}
