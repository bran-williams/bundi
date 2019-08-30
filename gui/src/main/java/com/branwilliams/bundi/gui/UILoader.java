package com.branwilliams.bundi.gui;

import com.branwilliams.bundi.gui.Component;
import com.branwilliams.bundi.gui.Container;
import com.branwilliams.bundi.gui.Layout;
import com.branwilliams.bundi.gui.components.Button;
import com.branwilliams.bundi.gui.containers.Frame;
import com.branwilliams.bundi.gui.layouts.ListLayout;
import com.branwilliams.bundi.gui.layouts.PaddedLayout;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    private LayoutFactory layoutFactory = new LayoutFactoryImpl();

    private Map<String, ComponentFactory> componentFactories = new HashMap<>();

    public UILoader() {
        componentFactories.put("frame", new FrameFactory());
        componentFactories.put("button", new ButtonFactory());
        //componentFactory.put("linearlayout", new LinearLayoutFactory());
    }

    public void loadUI(File file) throws IOException, SAXException, ParserConfigurationException,
            IllegalArgumentException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getDocumentElement().getChildNodes();

//        List<Container> containers = new ArrayList<>();
        if ("UI".equalsIgnoreCase(doc.getDocumentElement().getNodeName())) {
            loadComponents(null, doc.getDocumentElement(), nodeList);
        } else
            throw new IllegalArgumentException(
                    doc.getDocumentElement().getNodeName()
                    + ", "
                    + "The base element must be a valid layout.");
    }

    private void loadComponents(Container parent, Node base, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            NamedNodeMap attributes = node.getAttributes();
            String nodeName = node.getNodeName().toLowerCase();

            if (componentFactories.containsKey(nodeName)) {
                Component component = componentFactories.get(nodeName).createNamedItem(node, attributes);
                System.out.println(component);
                if (parent != null) {
                    parent.add(component);
                }

                if (component instanceof Container) {
                    loadComponents((Container) component, node, node.getChildNodes());
                }

            } else {
                Layout layout = layoutFactory.createNamedItem(node, attributes);

                if (parent != null && layout != null) {
                    parent.setLayout(layout);
                }
            }
        }
    }

    public class FrameFactory implements ComponentFactory {

        @Override
        public Component createNamedItem(Node node, NamedNodeMap attributes) {
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
    }

    public class ButtonFactory implements ComponentFactory {

        @Override
        public Component createNamedItem(Node node, NamedNodeMap attributes) {
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
    }

    private class LayoutFactoryImpl implements LayoutFactory {
        @Override
        public Layout createNamedItem(Node node, NamedNodeMap attributes) {
            switch (node.getNodeName().toLowerCase()) {
                case "listlayout":
                    int padding = getAttributeInt(attributes, "padding", 0);
                    int componentPadding = getAttributeInt(attributes, "componentPadding", padding);
                    boolean vertical = getAttributeBoolean(attributes, "vertical", true);
                    boolean forcedSize = getAttributeBoolean(attributes, "forcedSize", false);
                    ListLayout listLayout = new ListLayout(padding, componentPadding, vertical, forcedSize);
                    return listLayout;
            }
            return new PaddedLayout();
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

    public interface ComponentFactory extends NamedItemFactory<Component> {
    }

    public interface LayoutFactory extends NamedItemFactory<Layout> {

    }

    public interface NamedItemFactory<T> {
        T createNamedItem(Node node, NamedNodeMap attributes);
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
