package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.texture.Texture;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class SpriteSheet {

    private final Texture texture;

    private final DynamicVAO dynamicVao;

    // The size of each sprite.
    private final int spriteWidth, spriteHeight;

    // Number of sprites on the horizontal axis and vertical axis.
    private final int numSpritesHorizontal;
    private final int numSpritesVertical;

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
            // UV positions for sprite at index
            float u = (float) (index % numSpritesHorizontal) / (float) numSpritesHorizontal;
            float v = (float) Math.floor(index / numSpritesHorizontal) / (float) numSpritesVertical;
            //float y = (float) Math.floor(index / numSpritesVertical) / (float) numSpritesVertical;

            float vWidth = spriteWidth * xscale;
            float vHeight = spriteHeight * yscale;

            float vX = centeredSprite ? -vWidth / 2 : 0;
            float vY = centeredSprite ? -vHeight / 2 : 0;
            float vX1 = centeredSprite ? vWidth / 2 : vWidth;
            float vY1 = centeredSprite ? vHeight / 2 : vHeight;

            dynamicVao.begin();
            dynamicVao.addRect(vX, vY, vX1, vY1,
                    u, // u
                    v, // v
                    u + (float) (spriteWidth) / (float) texture.getWidth(),
                    v + (float) (spriteHeight) / (float) texture.getHeight(),
                    1F, 1F, 1F, 1F);
            sprites[index] = new Sprite(this, dynamicVao.getVertexFormat(), dynamicVao.pop(), dynamicVao.getVertexCount(),
                    index, spriteWidth * xscale, spriteHeight * yscale, centeredSprite);
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
        Sprite sprite;

        float u = (float) x / (float) texture.getWidth();
        float v = (float) y / (float) texture.getHeight();

        float vWidth = width * xscale;
        float vHeight = height * yscale;

        float vX = centeredSprite ? -vWidth / 2 : 0;
        float vY = centeredSprite ? -vHeight / 2 : 0;
        float vX1 = centeredSprite ? vWidth / 2 : vWidth;
        float vY1 = centeredSprite ? vHeight / 2 : vHeight;

        dynamicVao.begin();
        dynamicVao.addRect(vX, vY, vX1, vY1,
                u, v,
                u + (float) (width) / (float) texture.getWidth(),
                v + (float) (height) / (float) texture.getHeight(),
                1F, 1F, 1F, 1F);
        sprite = new Sprite(this, dynamicVao.getVertexFormat(), dynamicVao.pop(), dynamicVao.getVertexCount(),
                -1, width * xscale, height * yscale, centeredSprite);
        return sprite;
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
}
