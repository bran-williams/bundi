package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public abstract class ComponentFactory<T extends Component> implements UIElementFactory<T> {

    @Override
    public T createElement(Toolbox toolbox, Node node, NamedNodeMap attributes, int parentWidth, int parentHeight) {
//        T component = delegate.createElement(toolbox, node, attributes);
        T component = this.createComponent(toolbox, node, attributes);

        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        component.setTag(tag);

//        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributePos(attributes, "x", 0, parentWidth);
        component.setX(x);

        int y = XmlUtils.getAttributePos(attributes, "y", 0, parentHeight);
        component.setY(y);

        int width = XmlUtils.getAttributePos(attributes, "width", 100, parentWidth);
        component.setWidth(width);

        int height = XmlUtils.getAttributePos(attributes, "height", 20, parentHeight);
        component.setHeight(height);

        component.setMinimumWidth(width);
        component.setMinimumHeight(height);

//        int minWidth = XmlUtils.getAttributeInt(attributes, "minWidth", -1);
//        component.setMinimumWidth(minWidth);
//
//        int minHeight = XmlUtils.getAttributeInt(attributes, "minHeight", -1);
//        component.setMinimumHeight(minHeight);

        return component;
    }

    public abstract T createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes);

//    @Override
//    public UIElementType getType() {
//        return delegate.getType();
//    }
//
//    @Override
//    public String getName() {
//        return delegate.getName();
//    }
}
