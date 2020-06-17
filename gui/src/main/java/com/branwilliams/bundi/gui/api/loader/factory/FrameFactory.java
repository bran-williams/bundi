package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.containers.Frame;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class FrameFactory extends ComponentFactory<Frame> {

    @Override
    public Frame createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String title = XmlUtils.getAttributeText(attributes, "title", null);
        boolean alwaysLayoutChildren = XmlUtils.getAttributeBoolean(attributes, "alwayslayoutchildren", true);
        boolean autoLayout = XmlUtils.getAttributeBoolean(attributes, "autolayout", false);
        boolean layering = XmlUtils.getAttributeBoolean(attributes, "layering", false);
        boolean useLayoutSize = XmlUtils.getAttributeBoolean(attributes, "uselayoutsize", true);
        Frame frame = new Frame(title);

        frame.setAlwaysLayoutChildren(alwaysLayoutChildren);
        frame.setAutoLayout(autoLayout);
        frame.setLayering(layering);
        frame.setUseLayoutSize(useLayoutSize);
        return frame;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.CONTAINER;
    }

    @Override
    public String getName() {
        return "frame";
    }
}
