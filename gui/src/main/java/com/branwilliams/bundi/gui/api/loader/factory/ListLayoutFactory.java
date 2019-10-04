package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.layouts.ListLayout;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ListLayoutFactory implements UIElementFactory<ListLayout> {
    @Override
    public ListLayout createElement(Node node, NamedNodeMap attributes) {
        int padding = XmlUtils.getAttributeInt(attributes, "padding", 0);
        int componentPadding = XmlUtils.getAttributeInt(attributes, "componentPadding", padding);
        boolean vertical = XmlUtils.getAttributeBoolean(attributes, "vertical", true);
        boolean forcedSize = XmlUtils.getAttributeBoolean(attributes, "forcedSize", false);
        ListLayout listLayout = new ListLayout(padding, componentPadding, vertical, forcedSize);
        return listLayout;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.LAYOUT;
    }
}
