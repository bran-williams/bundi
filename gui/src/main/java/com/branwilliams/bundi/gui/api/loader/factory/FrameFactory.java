package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.containers.Frame;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class FrameFactory implements UIElementFactory<Frame> {

    @Override
    public Frame createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 100);
        String title = XmlUtils.getAttributeText(attributes, "title", null);
        boolean alwaysLayoutChildren = XmlUtils.getAttributeBoolean(attributes, "alwayslayoutchildren", true);
        boolean autoLayout = XmlUtils.getAttributeBoolean(attributes, "autolayout", false);
        boolean layering = XmlUtils.getAttributeBoolean(attributes, "layering", false);
        boolean useLayoutSize = XmlUtils.getAttributeBoolean(attributes, "uselayoutsize", true);
        Frame frame = new Frame(tag, title);

        frame.setAlwaysLayoutChildren(alwaysLayoutChildren);
        frame.setAutoLayout(autoLayout);
        frame.setLayering(layering);
        frame.setUseLayoutSize(useLayoutSize);
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
