package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.layouts.anchor.KeepChildrenInside;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class KeepChildrenInsideFactory implements UIElementFactory<KeepChildrenInside> {


    @Override
    public KeepChildrenInside createElement(Toolbox toolbox, Node node, NamedNodeMap attributes, int parentWidth, int parentHeight) {
        KeepChildrenInside keepChildrenInside = new KeepChildrenInside();
        return keepChildrenInside;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.LAYOUT;
    }

    @Override
    public String getName() {
        return "keepinside";
    }
}
