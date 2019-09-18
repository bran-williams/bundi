package com.branwilliams.bundi.gui;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.components.Button;
import com.branwilliams.bundi.gui.containers.Frame;
import com.branwilliams.bundi.gui.layouts.ListLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon Williams on 8/1/2018.
 */
public class UILoader {

    private static final String UI_BASE_ELEMENT = "ui";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, ElementFactory> elementFactories = new HashMap<>();

    public UILoader() {
        elementFactories.put("frame", new FrameFactory());
        elementFactories.put("button", new ButtonFactory());
        elementFactories.put("listlayout", new ListLayoutFactory());
        elementFactories.put("fontdata", new FontDataFactory());
    }

    public void loadUI(File file) throws IOException, SAXException, ParserConfigurationException,
            IllegalArgumentException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        List<Container> containers = new ArrayList<>();

        if (UI_BASE_ELEMENT.equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
            NodeList nodeList = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                String nodeName = node.getNodeName().toLowerCase();

                if (elementFactories.containsKey(nodeName)) {
                    ElementFactory elementFactory = elementFactories.get(nodeName);

                    if (elementFactory.getType() == UIElementType.CONTAINER) {
                        Container container = (Container) elementFactory.createElement(node, node.getAttributes());
                        loadContainerElements(container, node.getChildNodes());
                        containers.add(container);
                    } else {
                        log.error("Cannot create element '" + nodeName + "' without parent container!");
                    }

                } else {
                    log.error("Invalid element '" + nodeName + "' from file: " + file.getAbsolutePath());
                }
            }
        } else
            throw new IllegalArgumentException(
                    doc.getDocumentElement().getNodeName()
                    + ", "
                    + "The base element must of type '" + UI_BASE_ELEMENT + "'.");

        System.out.println(containers);

        // return container manager with list of containers?
    }

    private void loadContainerElements(Container parent, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            NamedNodeMap attributes = node.getAttributes();
            String nodeName = node.getNodeName().toLowerCase();

            if (elementFactories.containsKey(nodeName)) {
                ElementFactory elementFactory = elementFactories.get(nodeName);
                Object element = elementFactory.createElement(node, attributes);

                switch (elementFactory.getType()) {
                    case CONTAINER:
                        Container container = (Container) element;
                        loadContainerElements(container, node.getChildNodes());
                        parent.add(container);
                        break;
                    case COMPONENT:
                        Component component = (Component) element;
                        if (node.hasChildNodes()) {
                            loadComponentElements(component, node.getChildNodes());
                        }
                        parent.add(component);
                        break;
                    case LAYOUT:
                        Layout layout = (Layout) element;
                        parent.setLayout(layout);
                        break;
                    case FONT:
                        FontData font = (FontData) element;
                        parent.setFont(font);
                        break;
                }
            } else {
                log.error("Unable to create element '" + nodeName + "' for container.");
            }
        }
    }

    private void loadComponentElements(Component component, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            NamedNodeMap attributes = node.getAttributes();
            String nodeName = node.getNodeName().toLowerCase();

            if (elementFactories.containsKey(nodeName)) {
                ElementFactory elementFactory = elementFactories.get(nodeName);
                Object element = elementFactory.createElement(node, attributes);

                switch (elementFactory.getType()) {
                    case CONTAINER:
                        throw new IllegalArgumentException("Unable to add container '" + nodeName + "' to a component!");
                    case COMPONENT:
                        throw new IllegalArgumentException("Unable to add component '" + nodeName + "' to another component!");
                    case LAYOUT:
                        throw new IllegalArgumentException("Unable to set layout '" + nodeName + "' to a component!");
                    case FONT:
                        FontData font = (FontData) element;
                        component.setFont(font);
                        break;
                }
            } else {
                log.error("Unable to create element '" + nodeName + "' for component.");
            }
        }
    }

    public class FrameFactory implements ElementFactory<Frame> {

        @Override
        public Frame createElement(Node node, NamedNodeMap attributes) {
            String tag = getAttributeText(attributes, "tag", null);

            int x = getAttributeInt(attributes, "x", 0);
            int y = getAttributeInt(attributes, "y", 0);
            int width = getAttributeInt(attributes, "width", 100);
            int height = getAttributeInt(attributes, "height", 100);
            String title = getAttributeText(attributes, "title", null);
            Frame frame = new Frame(tag, title);

            frame.setX(x);
            frame.setY(y);
            frame.setWidth(width);
            frame.setHeight(height);
            return frame;
        }

        @Override
        public UIElementType getType() {
            return UIElementType.CONTAINER;
        }
    }

    public class ButtonFactory implements ElementFactory<Button> {

        @Override
        public Button createElement(Node node, NamedNodeMap attributes) {
            String tag = getAttributeText(attributes, "tag", null);

            int x = getAttributeInt(attributes, "x", 0);
            int y = getAttributeInt(attributes, "y", 0);
            int width = getAttributeInt(attributes, "width", 100);
            int height = getAttributeInt(attributes, "height", 20);
            String text = getAttributeText(attributes, "text", null);
            Button button = new Button(tag, text);

            button.setX(x);
            button.setY(y);
            button.setWidth(width);
            button.setHeight(height);
            return button;
        }

        @Override
        public UIElementType getType() {
            return UIElementType.COMPONENT;
        }

    }

    private class ListLayoutFactory implements ElementFactory<ListLayout> {
        @Override
        public ListLayout createElement(Node node, NamedNodeMap attributes) {
            int padding = getAttributeInt(attributes, "padding", 0);
            int componentPadding = getAttributeInt(attributes, "componentPadding", padding);
            boolean vertical = getAttributeBoolean(attributes, "vertical", true);
            boolean forcedSize = getAttributeBoolean(attributes, "forcedSize", false);
            ListLayout listLayout = new ListLayout(padding, componentPadding, vertical, forcedSize);
            return listLayout;
        }

        @Override
        public UIElementType getType() {
            return UIElementType.LAYOUT;
        }
    }

    private class FontDataFactory implements ElementFactory<FontData> {
        @Override
        public FontData createElement(Node node, NamedNodeMap attributes) {
            FontData fontData = new FontData();
            String font = getAttributeText(attributes, "font", "Default");
            int size = getAttributeInt(attributes, "size", 18);
            String style = getAttributeText(attributes, "style", "plain");
            boolean antialias = getAttributeBoolean(attributes, "antialias", true);
            return fontData;
            // for now..
            //return fontData.setFont(new Font(font, getStyle(style), size), antialias);
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
    }

    private static boolean getAttributeBoolean(NamedNodeMap attributes, String attribute, boolean defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Boolean.parseBoolean(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static int getAttributeInt(NamedNodeMap attributes, String attribute, int defaultValue) {
        String text = getAttributeText(attributes, attribute, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String getAttributeText(NamedNodeMap attributes, String attribute, String defaultText) {
        if (attributes == null) {
            return defaultText;
        }

        Node attributeNode = attributes.getNamedItem(attribute);

        return attributeNode == null ? defaultText : attributeNode.getTextContent();
    }

    public interface ElementFactory<T> {

        T createElement(Node node, NamedNodeMap attributes);

        UIElementType getType();
    }

    public enum UIElementType {
        CONTAINER,
        COMPONENT,
        LAYOUT,
        FONT;
    }

    public static void main(String[] args) {
        File file = new File("run/ui.xml");
        UILoader uiLoader = new UILoader();
        try {
            uiLoader.loadUI(file);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
