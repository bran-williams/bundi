package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
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
public class ButtonFactory extends ComponentFactory<Button> {

    @Override
    public Button createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        boolean active = XmlUtils.getAttributeBoolean(attributes, "active", true);

        Color buttonColor = XmlUtils.getAttributeColor(attributes, "color", null);
        Color textColor = XmlUtils.getAttributeColor(attributes, "textcolor", null);

        Button button = new Button(text, active);
        button.setBackgroundColor(buttonColor);
        button.setTextColor(textColor);
        return button;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "button";
    }
}
