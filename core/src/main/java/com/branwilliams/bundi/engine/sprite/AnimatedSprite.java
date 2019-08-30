package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.util.RateLimiter;

/**
 * Created by Brandon Williams on 10/8/2018.
 */
public class AnimatedSprite extends Sprite {

    private final RateLimiter rateLimiter;

    private final Sprite[] sprites;

    private int spriteIndex;

    public AnimatedSprite(SpriteSheet spriteSheet, int[] spriteIndices, RateLimiter rateLimiter, float scale) {
        this(spriteSheet, spriteIndices, rateLimiter, scale, scale);
    }

    public AnimatedSprite(SpriteSheet spriteSheet, int[] spriteIndices, RateLimiter rateLimiter, float xscale, float yscale) {
        super(spriteSheet, null, null, 0, 0, 0, 0, spriteSheet.isCenteredSprite());
        sprites = new Sprite[spriteIndices.length];
        for (int i = 0; i < spriteIndices.length; i++) {
            sprites[i] = spriteSheet.getSprite(spriteIndices[i], xscale, yscale);
        }
        this.rateLimiter = rateLimiter;
    }

    public Sprite getCurrentSprite() {
        updateSpriteIndex();
        return sprites[spriteIndex];
    }

    @Override
    public void draw() {
        getCurrentSprite().draw();
    }

    @Override
    public float getWidth() {
        return getCurrentSprite().getWidth();
    }

    @Override
    public float getHeight() {
        return getCurrentSprite().getHeight();
    }

    @Override
    public int getIndex() {
        return getCurrentSprite().getIndex();
    }

    public void updateSpriteIndex() {
        while (rateLimiter.reached()) {
            this.spriteIndex++;
            if (spriteIndex >= sprites.length) {
                spriteIndex = 0;
            }
        }
    }
}
