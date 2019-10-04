package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.components.Slider;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class SliderFactory implements UIElementFactory<Slider> {

    @Override
    public Slider createElement(Node node, NamedNodeMap attributes) {
        String tag = XmlUtils.getAttributeText(attributes, "tag", null);

        int x = XmlUtils.getAttributeInt(attributes, "x", 0);
        int y = XmlUtils.getAttributeInt(attributes, "y", 0);
        int width = XmlUtils.getAttributeInt(attributes, "width", 100);
        int height = XmlUtils.getAttributeInt(attributes, "height", 20);
        int barSize = XmlUtils.getAttributeInt(attributes, "barsize", 16);
        Slider slider = new Slider(tag, barSize);

        slider.setX(x);
        slider.setY(y);
        slider.setWidth(width);
        slider.setHeight(height);
        return slider;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

}
