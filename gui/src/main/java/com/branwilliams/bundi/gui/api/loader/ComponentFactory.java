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

//    private UIElementFactory<T> delegate;
//
//    public ComponentFactory(UIElementFactory<T> delegate) {
//        this.delegate = delegate;
//    }

    @Override
    public T createElement(Toolbox toolbox, Node node, NamedNodeMap attributes) {
//        T component = delegate.createElement(toolbox, node, attributes);
        T component = this.createComponent(toolbox, node, attributes);

        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        component.setTag(tag);

//        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        component.setX(x);

        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        component.setY(y);

        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        component.setWidth(width);

        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
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
