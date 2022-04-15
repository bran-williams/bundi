package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Checkbox;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class CheckboxFactory extends ComponentFactory<Checkbox> {

    @Override
    public Checkbox createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        boolean enabled = XmlUtils.getAttributeBoolean(attributes, "enabled", false);

        Checkbox checkbox = new Checkbox(text, enabled);

        return checkbox;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "checkbox";
    }
}
