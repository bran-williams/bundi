package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Stores font information can be rendered using a font renderer. <br/>
 * Created by Brandon on 9/26/2016.
 */
public final class SdfFontData extends FontData {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CharacterData[] characterBounds = new CharacterData[256];

    private int fontHeight = 0;

    // By default, 4 px is assumed for the width of a space character.
    private int spaceWidth = 4;

    public SdfFontData() {
    }

    public SdfFontData(FontDescription fontDescription) {
        setFont(fontDescription.toAwtFont(), fontDescription.isAntialias());
    }

    /**
     * Creates a fontData image and the character locations within the fontData image.
     * */
    @Override
    public FontData setFont(Font font, boolean antialias) {
        return setFont(font, antialias, antialias, 16, 2);
    }

    @Override
    protected CharacterData[] getCharacterBounds() {
        return characterBounds;
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

            characterBounds[i] = new CharacterData(this, positionX, positionY, width, height);

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
     * @return True if the character has been mapped in this font.
     * */
    public boolean hasBounds(char character) {
        return character >= 32 && character < 256;
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

}