package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ButtonFactory extends ComponentFactory<Button> {

    @Override
    public Button createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        boolean active = XmlUtils.getAttributeBoolean(attributes, "active", true);
        Color buttonColor = XmlUtils.getAttributeColor(attributes, "color", null);

        Button button = new Button(active);
        button.setBackgroundColor(buttonColor);
        button.setAlwaysLayoutChildren(true);
        button.setAutoLayout(true);
        button.setLayering(false);
        button.setUseLayoutSize(false);
        return button;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.CONTAINER;
    }

    @Override
    public String getName() {
        return "button";
    }
}
