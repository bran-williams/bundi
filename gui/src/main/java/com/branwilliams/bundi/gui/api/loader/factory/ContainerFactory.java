package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

import static com.branwilliams.bundi.gui.impl.Pointers.*;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ContainerFactory extends ComponentFactory<Container> {

    @Override
    public Container createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        boolean alwaysLayoutChildren = XmlUtils.getAttributeBoolean(attributes, "alwayslayoutchildren", true);
        boolean autoLayout = XmlUtils.getAttributeBoolean(attributes, "autolayout", false);
        boolean layering = XmlUtils.getAttributeBoolean(attributes, "layering", false);
        boolean useLayoutSize = XmlUtils.getAttributeBoolean(attributes, "uselayoutsize", true);
        float opacity = Math.max(0F, XmlUtils.getAttributeFloat(attributes, "opacity", 1F));
        Color backgroundColor = XmlUtils.getAttributeColor(attributes, "color", toolbox.get(COLOR_SECONDARY_BACKGROUND));

        Container container = new Container();
        container.setAlwaysLayoutChildren(alwaysLayoutChildren);
        container.setAutoLayout(autoLayout);
        container.setLayering(layering);
        container.setUseLayoutSize(useLayoutSize);
        container.setOpacity(opacity);
        container.setBackgroundColor(backgroundColor);
        return container;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.CONTAINER;
    }

    @Override
    public String getName() {
        return "container";
    }
}
