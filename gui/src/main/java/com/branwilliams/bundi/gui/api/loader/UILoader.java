package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Layout;
import com.branwilliams.bundi.gui.api.loader.factory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

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

    private static final String UI_BASE_ELEMENT = "ui";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, UIElementFactory> elementFactories = new HashMap<>();

    public UILoader(FontCache fontCache) {
        loadDefaultFactories(fontCache);
    }

    private void loadDefaultFactories(FontCache fontCache) {
        elementFactories.put("frame", new FrameFactory());
        elementFactories.put("container", new ContainerFactory());
        elementFactories.put("button", new ButtonFactory());
        elementFactories.put("slider", new SliderFactory());
        elementFactories.put("checkbox", new CheckboxFactory());
        elementFactories.put("listlayout", new ListLayoutFactory());
        elementFactories.put("gridlayout", new GridLayoutFactory());
        elementFactories.put("fontdata", new FontDataFactory(fontCache));
        elementFactories.put("label", new LabelFactory());
    }

    public List<Container> loadUI(File file) throws IOException, SAXException, ParserConfigurationException,
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
                    UIElementFactory elementFactory = elementFactories.get(nodeName);

                    if (elementFactory.getType() == UIElementType.CONTAINER) {
                        Container container = (Container) elementFactory.createElement(node, node.getAttributes());
                        loadContainerElements(container, node.getChildNodes());
                        container.layout();
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

        return containers;
    }

    private void loadContainerElements(Container parent, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            NamedNodeMap attributes = node.getAttributes();
            String nodeName = node.getNodeName().toLowerCase();

            if (elementFactories.containsKey(nodeName)) {
                UIElementFactory elementFactory = elementFactories.get(nodeName);
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
                UIElementFactory elementFactory = elementFactories.get(nodeName);
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

}
