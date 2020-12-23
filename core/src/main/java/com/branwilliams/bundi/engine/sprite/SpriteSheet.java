package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.texture.Texture;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class SpriteSheet implements Destructible {

    private final Texture texture;

    // The size of each sprite.
    private final int spriteWidth, spriteHeight;

    // Number of sprites on the horizontal axis and vertical axis.
    private final int numSpritesHorizontal;
    private final int numSpritesVertical;

    private DynamicVAO dynamicVao;

    private Sprite[] sprites;

    // When true, a sprites vertex positions will be centered around the 0,0 position.
    private boolean centeredSprite = true;

    public SpriteSheet(Texture texture, DynamicVAO dynamicVao, int spriteSize) {
        this(texture, dynamicVao, spriteSize, spriteSize);
    }

    public SpriteSheet(Texture texture, DynamicVAO dynamicVao, int spriteWidth, int spriteHeight) {
        if (texture.getWidth() % spriteWidth != 0 || texture.getHeight() % spriteHeight != 0) {
            throw new IllegalArgumentException("A spritesheet must have dimensions that are divisible by the sprite " +
                    "size!");
        }
        this.texture = texture;
        this.dynamicVao = dynamicVao;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.numSpritesHorizontal = texture.getWidth() / spriteWidth;
        this.numSpritesVertical = texture.getHeight() / spriteHeight;

        sprites = new Sprite[numSpritesHorizontal * numSpritesVertical];
    }

    public Sprite getSprite(int index) {
        return getSprite(index, 1F);
    }

    public Sprite getSprite(int index, float scale) {
        return getSprite(index, scale, scale);
    }

    /**
     * This function will create a sprite object for the given index or return the sprite object that already exists
     * for that index.
     * @return A Sprite object at the index specified.
     * */
    public Sprite getSprite(int index, float xscale, float yscale) {
        if (index < 0 || index >= sprites.length) {
            return null;
        }
        if (sprites[index] == null) {
            buildSpriteMesh(this, dynamicVao, index, xscale, yscale);

            sprites[index] = new Sprite(this, dynamicVao, index,
                    spriteWidth * xscale, spriteHeight * yscale, centeredSprite);

            this.dynamicVao = new DynamicVAO();
        }
        return sprites[index];
    }

    /**
     * See {@link }.
     * */
    public Sprite getSprite(int x, int y, int width, int height) {
        return getSprite(x, y, width, height, 1F, 1F);
    }

    /**
     * This function will create a sprite object for the given x, y position with the given width and height.
     * @param x The x coordinate of the sprite in the sprite texture.
     * @param y The y coordinate of the sprite.
     * @param width The width of the sprite.
     * @param height The height of the sprite.
     * @return A Sprite from this sprite sheet.
     * */
    public Sprite getSprite(int x, int y, int width, int height, float xscale, float yscale) {
        buildSpriteMesh(this, dynamicVao, x, y, width, height, xscale, yscale);

        Sprite sprite = new Sprite(this, dynamicVao,
                -1, width * xscale, height * yscale, centeredSprite);

        this.dynamicVao = new DynamicVAO();

        return sprite;

    }

    public float getSpriteU(int index) {
        return getSpriteU(this, index);
    }

    public float getSpriteV(int index) {
        return getSpriteV(this, index);
    }

    public float getSpriteS(int index) {
        return getSpriteS(this, index);
    }

    public float getSpriteT(int index) {
        return getSpriteT(this, index);
    }

    public static float getSpriteU(SpriteSheet spriteSheet, int index) {
        return (float) (index % spriteSheet.numSpritesHorizontal) / (float) spriteSheet.numSpritesHorizontal;
    }

    public static float getSpriteV(SpriteSheet spriteSheet, int index) {
        return (float) Math.floor(index / spriteSheet.numSpritesHorizontal) / (float) spriteSheet.numSpritesVertical;
    }

    public static float getSpriteS(SpriteSheet spriteSheet, int index) {
        return getSpriteU(spriteSheet, index) +
                (float) (spriteSheet.spriteWidth) / (float) spriteSheet.texture.getWidth();
    }

    public static float getSpriteT(SpriteSheet spriteSheet, int index) {
        return getSpriteV(spriteSheet, index) +
                (float) (spriteSheet.spriteHeight) / (float) spriteSheet.texture.getHeight();
    }

    /**
     * This function will create a sprite object for the given x, y position with the given width and height in pixels.
     *
     * */
    public static void buildSpriteMesh(SpriteSheet spriteSheet, DynamicVAO dynamicVao, int index,
                                       float xscale, float yscale) {
        // UV positions for sprite at index
        float u = getSpriteU(spriteSheet, index);
        float v = getSpriteV(spriteSheet, index);
        float s = getSpriteS(spriteSheet, index);
        float t = getSpriteT(spriteSheet, index);

        float vWidth = spriteSheet.spriteWidth * xscale;
        float vHeight = spriteSheet.spriteHeight * yscale;

        float vX = spriteSheet.centeredSprite ? -vWidth / 2 : 0;
        float vY = spriteSheet.centeredSprite ? -vHeight / 2 : 0;
        float vX1 = spriteSheet.centeredSprite ? vWidth / 2 : vWidth;
        float vY1 = spriteSheet.centeredSprite ? vHeight / 2 : vHeight;

        dynamicVao.begin();
        dynamicVao.addRect(vX, vY, vX1, vY1,
                u, v, s, t,
                1F, 1F, 1F, 1F);
        dynamicVao.compile();
    }

    /**
     * This function will create a sprite object for the given x, y position with the given width and height in pixels.
     *
     * @param x The x coordinate of the sprite.
     * @param y The y coordinate of the sprite.
     * @param width The width of the sprite.
     * @param height The height of the sprite.
     * */
    public static void buildSpriteMesh(SpriteSheet spriteSheet, DynamicVAO dynamicVao, int x, int y,
                                         int width, int height, float xscale, float yscale) {
        float u = (float) x / (float) spriteSheet.texture.getWidth();
        float v = (float) y / (float) spriteSheet.texture.getHeight();
        float s = u + (float) (width) / (float) spriteSheet.texture.getWidth();
        float t = v + (float) (height) / (float) spriteSheet.texture.getHeight();

        float vWidth = width * xscale;
        float vHeight = height * yscale;

        float vX = spriteSheet.centeredSprite ? -vWidth * 0.5F : 0;
        float vY = spriteSheet.centeredSprite ? -vHeight * 0.5F : 0;
        float vX1 = spriteSheet.centeredSprite ? vWidth * 0.5F : vWidth;
        float vY1 = spriteSheet.centeredSprite ? vHeight * 0.5F : vHeight;

        dynamicVao.begin();
        dynamicVao.addRect(vX, vY, vX1, vY1,
                u, v, s, t,
                1F, 1F, 1F, 1F);
        dynamicVao.compile();
    }


    /**
     * Destroys the sprite object at the given index.
     * */
    public boolean destroySprite(int index) {
        if (index < 0 || index >= sprites.length || sprites[index] == null) {
            return false;
        }
        sprites[index].destroy();
        sprites[index] = null;
        return true;
    }

    public Integer getIndex(int x, int y) {
        return x + y * texture.getWidth();
    }

    public Integer getMaxIndex() {
        return this.sprites.length - 1;
    }

    public Texture getTexture() {
        return texture;
    }

    /**
     * @return The total number of sprites for this spritesheet.
     * */
    public int getTotalSprites() {
        return numSpritesHorizontal * numSpritesVertical;
    }

    public boolean isCenteredSprite() {
        return centeredSprite;
    }

    public void setCenteredSprite(boolean centeredSprite) {
        this.centeredSprite = centeredSprite;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getNumSpritesHorizontal() {
        return numSpritesHorizontal;
    }

    public int getNumSpritesVertical() {
        return numSpritesVertical;
    }

    @Override
    public void destroy() {
        this.texture.destroy();
        this.dynamicVao.destroy();

        for (int i = 0; i < sprites.length; i++) {
            destroySprite(i);
        }
    }
}
