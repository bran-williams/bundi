package com.branwilliams.bundi.gui.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public enum XmlUtils {
    INSTANCE;

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
