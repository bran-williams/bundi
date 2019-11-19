package com.branwilliams.bundi.gui.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public enum XmlUtils {
    INSTANCE;

    public static Color getAttributeColor(NamedNodeMap attributes, String attribute, Color defaultValue) {
        String text = getAttributeText(attributes, attribute, null);

        if (text == null)
            return defaultValue;

        try {
            return Color.decode(text);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    public static boolean getAttributeBoolean(NamedNodeMap attributes, String attribute, boolean defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Boolean.parseBoolean(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getAttributeInt(NamedNodeMap attributes, String attribute, int defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getAttributeText(NamedNodeMap attributes, String attribute, String defaultText) {
        if (attributes == null) {
            return defaultText;
        }

        Node attributeNode = attributes.getNamedItem(attribute);

        return attributeNode == null ? defaultText : attributeNode.getTextContent();
    }
}
