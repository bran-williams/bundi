package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.layouts.GridLayout;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class GridLayoutFactory implements UIElementFactory<GridLayout> {

    @Override
    public GridLayout createElement(Node node, NamedNodeMap attributes) {
        int rows = XmlUtils.getAttributeInt(attributes, "rows", 0);
        int columns = XmlUtils.getAttributeInt(attributes, "columns", 0);

        // Padding is used whenever horizontal and vertical padding are not defined.
        int padding = XmlUtils.getAttributeInt(attributes, "padding", 0);
        int horizontalPadding = XmlUtils.getAttributeInt(attributes, "horizontalpadding", padding);
        int verticalPadding = XmlUtils.getAttributeInt(attributes, "verticalpadding", padding);

        // Padding is also used for component padding whenever it is not defined.
        int componentPadding = XmlUtils.getAttributeInt(attributes, "componentpadding", padding);

        GridLayout gridLayout = new GridLayout(rows, columns, horizontalPadding, verticalPadding, componentPadding);
        return gridLayout;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.LAYOUT;
    }
}
