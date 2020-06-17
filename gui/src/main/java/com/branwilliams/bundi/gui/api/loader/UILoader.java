package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.gui.api.*;
import com.branwilliams.bundi.gui.api.loader.factory.*;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon Williams on 8/1/2018.
 */
public class UILoader {

    public static final String UI_BASE_ELEMENT = "ui";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TemplateEvaluator templateEvaluator;

    private final Toolbox toolbox;

    private final Path directory;

    private Map<String, UIElementFactory> elementFactories = new HashMap<>();

    public UILoader(FontCache fontCache, Toolbox toolbox) {
        this.toolbox = toolbox;

        EngineContext context = toolbox.getEngine().getContext();
        this.directory = context.getAssetDirectory();

        this.templateEvaluator = TemplateEvaluator.getDefault(directory);

        loadDefaultFactories(fontCache, context);
    }

    private void loadDefaultFactories(FontCache fontCache, EngineContext context) {
        addComponentFactory(new FrameFactory());
        addComponentFactory(new ContainerFactory());
        addComponentFactory(new ButtonFactory());
        addComponentFactory(new SliderFactory());
        addComponentFactory(new CheckboxFactory());
        addComponentFactory(new LabelFactory());
        addComponentFactory(new TextFieldFactory());
        addComponentFactory(new ImageFactory(context));

        addElementFactory(new ListLayoutFactory());
        addElementFactory(new GridLayoutFactory());
        addElementFactory(new FontDataFactory(fontCache));
        addElementFactory(new PopupContainerFactory());

    }

    public void addElementFactory(UIElementFactory<?> elementFactory) {
        elementFactories.put(elementFactory.getName(), elementFactory);
    }

    public void addComponentFactory(UIElementFactory<?> componentFactory) {
        addElementFactory(componentFactory);
    }

    public List<Container> loadUI(String file, Map<String, Object> env) throws IOException, SAXException, ParserConfigurationException,
            IllegalArgumentException {
        return loadUI(Paths.get(file), env);
    }

    public List<Container> loadUI(Path file, Map<String, Object> env) throws IOException, SAXException, ParserConfigurationException,
            IllegalArgumentException {
        String fileContents = IOUtils.readFile(directory, file.toString(), null);
        if (fileContents == null) {
            log.error("Unable to read file " + directory.resolve(file.toString()));
            return null;
        }

        Document document = XmlUtils.fromString(fileContents);
        Node documentElement = document.getDocumentElement();

        List<Container> containers = new ArrayList<>();
        if (UI_BASE_ELEMENT.equalsIgnoreCase(documentElement.getNodeName())) {

            Node root = templateEvaluator.applyTemplateToDocument(document, null, documentElement, env);
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                String nodeName = node.getNodeName().toLowerCase();

                if (elementFactories.containsKey(nodeName)) {
                    UIElementFactory elementFactory = elementFactories.get(nodeName);

                    if (elementFactory.getType() == UIElementType.CONTAINER) {
                        Container container = (Container) elementFactory.createElement(toolbox, node, node.getAttributes());
                        loadContainerElements(container, node.getChildNodes());
                        container.layout();
                        containers.add(container);
                    } else {
                        log.error("Cannot create element '" + nodeName + "' without parent container!");
                    }

                } else {
                    log.error("Invalid element '" + nodeName + "' from file: " + file);
                }
            }
        } else
            throw new IllegalArgumentException(documentElement.getNodeName()
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
                Object element = elementFactory.createElement(toolbox, node, attributes);

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
                    case POPUP:
                        PopupContainer popupContainer = (PopupContainer) element;
                        loadContainerElements(popupContainer, node.getChildNodes());
                        parent.setTooltip(popupContainer);
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
                Object element = elementFactory.createElement(toolbox, node, attributes);

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
                    case POPUP:
                        PopupContainer popupContainer = (PopupContainer) element;
                        loadContainerElements(popupContainer, node.getChildNodes());
                        component.setTooltip(popupContainer);
                        break;
                }
            } else {
                log.error("Unable to create element '" + nodeName + "' for component.");
            }
        }
    }

}
