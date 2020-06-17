package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class FontDataFactory implements UIElementFactory<FontData> {

    private FontCache fontCache;

    public FontDataFactory(FontCache fontCache) {
        this.fontCache = fontCache;
    }

    @Override
    public FontData createElement(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String font = XmlUtils.getAttributeText(attributes, "font", "Default");
        int size = XmlUtils.getAttributeInt(attributes, "size", 18);
        String style = XmlUtils.getAttributeText(attributes, "style", "plain");
        boolean antialias = XmlUtils.getAttributeBoolean(attributes, "antialias", true);

        return fontCache.createFont(font, getStyle(style), size, antialias);
    }

    private int getStyle(String style) {
        switch (style.toLowerCase()) {
            case "bold":
                return Font.BOLD;
            case "italic":
                return Font.ITALIC;
            // If the style is set to plain, then this will always return.
            default:
                return Font.PLAIN;
        }
    }

    @Override
    public UIElementType getType() {
        return UIElementType.FONT;
    }

    @Override
    public String getName() {
        return "fontdata";
    }
}
