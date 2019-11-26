package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ButtonFactory implements UIElementFactory<Button> {

    @Override
    public Button createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        boolean highlight = XmlUtils.getAttributeBoolean(attributes, "highlight", false);

        Button button = new Button(tag, text);
        button.setTooltip(tooltip);
        button.setHighlight(highlight);
        button.setX(x);
        button.setY(y);
        button.setWidth(width);
        button.setHeight(height);
        return button;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

}
