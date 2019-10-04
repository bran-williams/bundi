package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.components.Checkbox;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class CheckboxFactory implements UIElementFactory<Checkbox> {

    @Override
    public Checkbox createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        boolean enabled = XmlUtils.getAttributeBoolean(attributes, "enabled", false);

        Checkbox checkbox = new Checkbox(tag, text, enabled);
        checkbox.setX(x);
        checkbox.setY(y);
        checkbox.setWidth(width);
        checkbox.setHeight(height);
        return checkbox;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

}
