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
public final class FontData implements Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CharacterData[] characterBounds = new CharacterData[256];

    private final CharacterData empty = new CharacterData(0, 0, 0, 0);

    private Texture texture;

    private int fontHeight = 0;

    // By default, 4 px is assumed for the width of a space character.
    private int spaceWidth = 4;

    /**
     * Creates a fontData image and the character locations within the fontData image.
     * */
    public FontData setFont(Font font, boolean antialias) {
        return setFont(font, antialias, antialias, 16, 2);
    }

    /**
     * Creates a font image and the character locations within the font image.
     * */
    private FontData setFont(Font font, boolean antialias, boolean fractionalmetrics, int characterCount, int padding) {
        // FontData metrics can be created from the font without having to create a graphics object.
        FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
        spaceWidth = fontMetrics.charWidth(' ');

        int charHeight = 0, positionX = 0, positionY = 0, imageWidth = 0, imageHeight = 0; //, textureWidth = 0, textureHeight = 0

        // We'll be generating the character bounds as well as an appropriate texture width and height for the font to be rendered onto.
        // Characters 0-32 are ascii fancy boys, so we don't need 'em.
        for (int i = 32; i < characterBounds.length; i++) {
            char character = (char) i;

            int height = fontMetrics.getHeight();
            int width = fontMetrics.charWidth(character);

            if (i % characterCount == 0) {
                positionX = padding;
                positionY += charHeight + padding;
                charHeight = 0;
            }

            if (height > charHeight) {
                charHeight = height;
                if (charHeight > fontHeight)
                    fontHeight = charHeight;
            }

            characterBounds[i] = new CharacterData(positionX, positionY, width, height);

            // Ensure that our texture can fit the characters.
            if (positionX + width + padding > imageWidth)
                imageWidth = positionX + width + padding;

            if (positionY + height + padding > imageHeight)
                imageHeight = positionY + height + padding;

            positionX += width + padding;
        }

        // Image we'll use to store our font onto for rendering.
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setFont(font);
        fontMetrics = graphics2D.getFontMetrics(font);

        // Give blank background
        graphics2D.setColor(new Color(255, 255, 255, 0));
        graphics2D.fillRect(0, 0, imageWidth, imageHeight);

        // Set color to white for rendering the font onto the texture.
        graphics2D.setColor(Color.WHITE);

        // Set render hints
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalmetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antialias ? RenderingHints.VALUE_TEXT_ANTIALIAS_GASP : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, antialias ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        for (int i = 32; i < characterBounds.length; i++) {
            // Draw the char onto the final image we'll be using to render this font.
            graphics2D.drawString(String.valueOf((char) i), characterBounds[i].x, characterBounds[i].y + fontMetrics.getAscent());
        }

        // Create an array to hold the pixels of the loaded image.
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        // Create a basic image and put the buffered images pixels into the pixel array.
        texture = new Texture(bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(),
                pixels, 0, bufferedImage.getWidth()), bufferedImage.getWidth(), bufferedImage.getHeight(), true);

        log.info(String.format("FontData created for font %s of size %d with dimensions: %dx%d.", font.getName(),
                font.getSize(), bufferedImage.getWidth(), bufferedImage.getHeight()));
        return this;
    }

    /**
     * Binds the font texture.
     * */
    public void bind() {
        glActiveTexture(GL_TEXTURE0);
		texture.bind();
    }

    /**
     * @return The bounds of the character within the font image.
     * */
    public CharacterData getCharacterBounds(char character) {
        if (!hasFont() || !hasBounds(character))
            return empty;
        return characterBounds[character];
    }

    /**
     * @return The total width of each character within the string.
     * */
    public int getStringWidth(String text) {
        if (!hasFont())
            return 0;
        int width = 0;
        for (char c : text.toCharArray()) {
            if (characterBounds[c] != null)
                width += characterBounds[c].width;
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
            if (characterBounds[c] != null && characterBounds[c].height > height)
                height = characterBounds[c].height;
        }
        return height;
    }

    /**
     * @return True if the character has been mapped in this font.
     * */
    public boolean hasBounds(char character) {
        return character >= 32 && character < 256;
    }

    /**
     * @return True if the font has not been set.
     * */
    public boolean hasFont() {
        return texture != null;
    }

    /**
     * @return The largest height possible for each character.
     * */
    public int getFontHeight() {
        return fontHeight;
    }

    /**
     * @return The width of a space character for this font.
     * */
    public int getSpaceWidth() {
        return spaceWidth;
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
            if (current + characterBounds[characters[index]].width > length&& index > 0) {
                // Return the substring of the index - 1, since the current index is too long.
                return backwards ? input.substring(index + 1, input.length()) : input.substring(0, index - 1);
            }
            current += characterBounds[characters[index]].width;
            index += backwards ? -1 : 1;
        }
        return input;
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
     * Character information regarding it's position within the font texture and the character's width/height within the font.
     * */
    public class CharacterData {

        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public CharacterData(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

}
