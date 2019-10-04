package com.branwilliams.bundi.engine.font;

import java.awt.*;
import java.util.Objects;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
class FontDescription {

    private final String fontName;

    private final int style;

    private final int size;

    private final boolean antialias;

    public FontDescription(String fontName, int style, int size, boolean antialias) {
        if (fontName == null)
            throw new IllegalArgumentException("Font name cannot be null!");
        this.fontName = fontName;
        this.style = style;
        this.size = size;
        this.antialias = antialias;
    }

    /***
     * @return A {@link java.awt.Font} object matching this font description.
     */
    public Font toAwtFont() {
        return new Font(fontName, style, size);
    }

    public String getFontName() {
        return fontName;
    }

    public int getSize() {
        return size;
    }

    public int getStyle() {
        return style;
    }

    public boolean isAntialias() {
        return antialias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontDescription)) return false;
        FontDescription that = (FontDescription) o;
        return getStyle() == that.getStyle() &&
                getSize() == that.getSize() &&
                isAntialias() == that.isAntialias() &&
                getFontName().equals(that.getFontName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFontName(), getStyle(), getSize(), isAntialias());
    }
}
