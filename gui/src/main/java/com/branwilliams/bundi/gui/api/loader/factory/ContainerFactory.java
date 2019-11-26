package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.containers.Frame;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ContainerFactory implements UIElementFactory<Container> {

    @Override
    public Container createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 0);
        int height = XmlUtils.getAttributeInt(attributes, "height", 0);
        boolean alwaysLayoutChildren = XmlUtils.getAttributeBoolean(attributes, "alwayslayoutchildren", true);
        boolean autoLayout = XmlUtils.getAttributeBoolean(attributes, "autolayout", false);
        boolean layering = XmlUtils.getAttributeBoolean(attributes, "layering", false);
        boolean useLayoutSize = XmlUtils.getAttributeBoolean(attributes, "uselayoutsize", true);
        Container container = new Container(tag);
        container.setTooltip(tooltip);
        container.setAlwaysLayoutChildren(alwaysLayoutChildren);
        container.setAutoLayout(autoLayout);
        container.setLayering(layering);
        container.setUseLayoutSize(useLayoutSize);
        container.setX(x);
        container.setY(y);
        container.setWidth(width);
        container.setHeight(height);
        return container;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.CONTAINER;
    }
}
