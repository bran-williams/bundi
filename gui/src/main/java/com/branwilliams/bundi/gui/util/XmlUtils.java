package com.branwilliams.bundi.gui.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public enum XmlUtils {
    INSTANCE;

    private static final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();


    public static Document fromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static void forAttributes(Node node, Consumer<Node> attributeConsumer) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null)
            return;

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            attributeConsumer.accept(attribute);
        }
    }

    public static void forChildren(Node node, Consumer<Node> childConsumer) {
        NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node child = childList.item(i);
            childConsumer.accept(child);
        }
    }
    public static void removeChildren(Node node, Predicate<Node> predicate) {
        forChildren(node, (child) -> {
            if (predicate.test(child)) {
                node.removeChild(child);
            }
        });
    }

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

    public static double getAttributeDouble(NamedNodeMap attributes, String attribute, double defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float getAttributeFloat(NamedNodeMap attributes, String attribute, float defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Float.parseFloat(text);
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
