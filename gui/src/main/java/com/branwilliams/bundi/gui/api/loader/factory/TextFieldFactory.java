package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.components.Checkbox;
import com.branwilliams.bundi.gui.api.components.TextField;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class TextFieldFactory implements UIElementFactory<TextField> {

    @Override
    public TextField createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
        String text = XmlUtils.getAttributeText(attributes, "text", "");
        String defaultText = XmlUtils.getAttributeText(attributes, "defaulttext", "");

        TextField textField = new TextField(tag, text, defaultText);
        textField.setTooltip(tooltip);
        textField.setX(x);
        textField.setY(y);
        textField.setWidth(width);
        textField.setHeight(height);
        return textField;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

}