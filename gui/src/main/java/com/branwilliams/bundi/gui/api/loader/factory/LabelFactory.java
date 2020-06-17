package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.impl.Pointers;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;

import static com.branwilliams.bundi.gui.impl.Pointers.COLOR_DEFAULT_TEXT;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class LabelFactory extends ComponentFactory<Label> {

    @Override
    public Label createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String text = XmlUtils.getAttributeText(attributes, "text", null);
        Color color = XmlUtils.getAttributeColor(attributes, "color", toolbox.get(COLOR_DEFAULT_TEXT));

        Label.LabelAlignment alignment = Label.LabelAlignment.fromOrDefault(
                XmlUtils.getAttributeText(attributes, "alignment", ""),
                Label.LabelAlignment.LEFT);

        Label.RenderStyle renderStyle = Label.RenderStyle.fromOrDefault(
                XmlUtils.getAttributeText(attributes, "style", ""),
                Label.RenderStyle.NONE);

        Label label = new Label(text);
        label.setAlignment(alignment);
        label.setRenderStyle(renderStyle);
        label.setText(text);
        label.setColor(color);
        return label;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "label";
    }
}
