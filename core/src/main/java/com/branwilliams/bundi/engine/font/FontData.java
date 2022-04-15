package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Stores font information can be rendered using a font renderer. <br/>
 * Created by Brandon on 9/26/2016.
 */
public abstract class FontData implements Destructible {

    public static final CharacterData EMPTY = new CharacterData(null, 0, 0, 0, 0);

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected Texture texture;

    public FontData() {
    }

    public FontData(FontDescription fontDescription) {
        setFont(fontDescription.toAwtFont(), fontDescription.isAntialias());
    }

    /**
     * Creates a fontData image and the character locations within the fontData image.
     * */
    public abstract FontData setFont(Font font, boolean antialias);


    protected abstract CharacterData[] getCharacterBounds();

    /**
     * @return The largest height possible for each character.
     * */
    public abstract int getFontHeight();

    /**
     * @return The width of a space character for this font.
     * */
    public abstract int getSpaceWidth();

    /**
     * @return The bounds of the character within the font image.
     * */
    public CharacterData getCharacterBounds(char character) {
        if (!hasFont() || !hasBounds(character))
            return EMPTY;
        return getCharacterBounds()[character];
    }

    /**
     * @return The total width of each character within the string.
     * */
    public int getStringWidth(String text) {
        if (!hasFont())
            return 0;
        int width = 0;
        for (char c : text.toCharArray()) {
            if (getCharacterBounds()[c] != null)
                width += getCharacterBounds()[c].width;
        }
        return width;
    }

    /**
     * @return The total width of each character within the string.
     * */
    public int getStringHeight(String text) {
        if (!hasFont())
            return 0;
        int height = 0;
        for (char c : text.toCharArray()) {
            if (getCharacterBounds()[c] != null && getCharacterBounds()[c].height > height)
                height = getCharacterBounds()[c].height;
        }
        return height;
    }

    /**
     * @return True if the character has been mapped in this font.
     * */
    public abstract boolean hasBounds(char character);

    /**
     * Binds the font texture.
     * */
    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
    }

    /**
     * @return True if the font has not been set.
     * */
    public boolean hasFont() {
        return texture != null;
    }

    /**
     * @return The image used by this font data.
     * */
    public Texture getTexture() {
        return texture;
    }

    @Override
    public void destroy() {
        this.texture.destroy();
    }

    /**
     * @return The input trimmed to fit the length expected.
     * */
    public String trim(String input, int length) {
        return this.trim(input, length, false);
    }

    /**
     * @return The input trimmed to fit the length expected.
     * */
    public String trim(String input, int length, boolean backwards) {
        if (!hasFont())
            return input;
        char[] characters = input.toCharArray();
        // Our start index will be 0 if we are trimming from the beginning and length - 1 if we are trimming from the end.
        int index = backwards ? characters.length - 1 : 0, current = 0;

        while (backwards ? index >= 0 : index < characters.length) {
            // If the current string length + the next character is too long, then go backward an index and return the trimmed string.
            // Length is multiplied by the game's scale factor to ensure this calculation is correct.
            if (current + getCharacterBounds()[characters[index]].width > length&& index > 0) {
                // Return the substring of the index - 1, since the current index is too long.
                return backwards ? input.substring(index + 1, input.length()) : input.substring(0, index - 1);
            }
            current += getCharacterBounds()[characters[index]].width;
            index += backwards ? -1 : 1;
        }
        return input;
    }
    /**
     * Character information regarding it's position within the font texture and the character's width/height within the font.
     * */
    public static class CharacterData {

        private final FontData fontData;

        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public CharacterData(FontData fontData, int x, int y, int width, int height) {
            this.fontData = fontData;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public float getU() {
            return (float) x / fontData.getTexture().getWidth();
        }

        public float getV() {
            return (float) y / fontData.getTexture().getHeight();
        }

        public float getS() {
            return (float) (x + width) / fontData.getTexture().getWidth();
        }

        public float getT() {
            return (float) (y + height) / fontData.getTexture().getHeight();
        }
    }

}
