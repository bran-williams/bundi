package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.gui.api.Toolbox;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public interface UIElementFactory<T> extends Nameable {

    T createElement(Toolbox toolbox, Node node, NamedNodeMap attributes, int parentWidth, int parentHeight);

    UIElementType getType();
}
