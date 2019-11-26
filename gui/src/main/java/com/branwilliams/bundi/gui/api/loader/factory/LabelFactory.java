package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class LabelFactory implements UIElementFactory<Label> {

    @Override
    public Label createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);
        String tooltip = XmlUtils.getAttributeText(attributes, "tooltip", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        Color color = XmlUtils.getAttributeColor(attributes, "color", Color.WHITE);
        Label.LabelAlignment alignment = Label.LabelAlignment.fromOrDefault(XmlUtils.getAttributeText(attributes, "alignment", ""), Label.LabelAlignment.LEFT);

        Label label = new Label(tag, text);
        label.setTooltip(tooltip);
        label.setAlignment(alignment);
        label.setText(text);
        label.setColor(color);
        label.setX(x);
        label.setY(y);
        label.setWidth(width);
        label.setHeight(height);
        return label;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

}
