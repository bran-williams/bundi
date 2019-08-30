package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.core.Destructible;

import java.awt.*;

/**
 * Renders strings at given x,y positions with the given hexadecimal color.
 * */
public interface FontRenderer extends Destructible {

	/**
	 * Draws a string with the provided {@link FontData} at the provided x and y locations.
	 * @param fontData The font data this renderer uses.
	 * @param text The text to render.
	 * @param x The x location of the text.
	 * @param y The y location of the text.
	 * @param color The hexadecimal color to render the text with.
	 * @return The width (in pixels) of the text rendered.
	 * */
	int drawString(FontData fontData, String text, int x, int y, int color);

    /**
	 * Draws a string with the provided {@link FontData} at the provided x and y locations.
	 * @see FontRenderer#drawString(FontData, String, int, int, int)
     * */
	default int drawString(String text, int x, int y, int color) {
		return drawString(getFontData(), text, x, y, color);
	}

	/**
	 * Draws a string the same as {@link FontRenderer#drawString(FontData, String, int, int, int)} with the addition of
	 * the same text behind it, but in black and offset by one pixel in the x and y directions.
	 * Draws a string with the provided {@link FontData} at the provided x and y locations.
	 * */
	default int drawStringWithShadow(FontData fontData, String text, int x, int y, int color) {
		int length = drawString(fontData, text, x + 1, y + 1,0xFF000000);
		return Math.max(length, drawString(fontData, text, x, y, color));
	}

	/**
	 *
	 * */
	default int drawStringWithShadow(String text, int x, int y, int color) {
		return drawStringWithShadow(getFontData(), text, x, y, color);
	}
	/**
	 *
	 * */
	default int drawCenteredString(FontData fontData, String text, int x, int y, int color) {
		return drawString(fontData, text,
				x - (int) (fontData.getStringWidth(text) * 0.5F),
				y - (int) (fontData.getFontHeight() * 0.5F), color);
	}

	/**
	 *
	 * */
	default int drawCenteredString(String text, int x, int y, int color) {
		return drawCenteredString(getFontData(), text, x, y, color);
	}

    /**
     * @return The default {@link FontData} used by this FontRenderer.
     * */
    FontData getFontData();

    /**
	 * Setter for the default {@link FontData} used by this FontRenderer.
	 * */
    void setFontData(FontData fontData);

    /**
	 * Wrapper for getFontData().setFont(font, antialias)
	 * */
    default FontData setFont(Font font, boolean antialias) {
    	return getFontData().setFont(font, antialias);
	}

}
