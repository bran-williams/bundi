package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.core.Destructible;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class FontCache implements Destructible {

    private Map<FontDescription, FontData> fonts = new HashMap<>();

    public FontCache() {

    }

    public FontData createFont(String fontName, int style, int size, boolean antialias) {
        FontDescription description = new FontDescription(fontName, style, size, antialias);
        if (fonts.containsKey(description)) {
            return fonts.get(description);
        } else {
            FontData fontData = new FontData(description);
            fonts.put(description, fontData);
            return fontData;
        }
    }

    @Override
    public void destroy() {
        for (FontData font : fonts.values()) {
            font.destroy();
        }
        fonts.clear();
    }

}
