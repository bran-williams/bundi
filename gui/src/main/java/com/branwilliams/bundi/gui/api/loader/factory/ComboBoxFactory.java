package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Checkbox;
import com.branwilliams.bundi.gui.api.components.ComboBox;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ComboBoxFactory extends ComponentFactory<ComboBox<String>> {

    private static final String ITEM_NODE_NAME = "item";

    @Override
    public ComboBox<String> createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        List<String> itemNames = new ArrayList<>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (ITEM_NODE_NAME.equalsIgnoreCase(childNode.getNodeName())) {
                itemNames.add(XmlUtils.getAttributeText(childNode.getAttributes(), "name",
                        "no_name" + i));
            }
        }

        ComboBox<String> comboBox = new ComboBox<>(itemNames.toArray(new String[0]));

        return comboBox;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "combobox";
    }
}
