package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Slider;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class SliderFactory extends ComponentFactory<Slider> {

    @Override
    public Slider createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        int barSize = XmlUtils.getAttributeInt(attributes, "barsize", 16);
        Slider slider = new Slider(barSize);
        return slider;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "slider";
    }
}
