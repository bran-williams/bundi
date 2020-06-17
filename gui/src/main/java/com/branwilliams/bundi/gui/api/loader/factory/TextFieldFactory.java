package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.TextField;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class TextFieldFactory extends ComponentFactory<TextField> {

    @Override
    public TextField createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String text = XmlUtils.getAttributeText(attributes, "text", "");
        String defaultText = XmlUtils.getAttributeText(attributes, "defaulttext", "");

        TextField textField = new TextField(text, defaultText);
        return textField;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "textfield";
    }
}
