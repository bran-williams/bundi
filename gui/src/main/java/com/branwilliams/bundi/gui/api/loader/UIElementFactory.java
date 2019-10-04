package com.branwilliams.bundi.gui.api.loader;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public interface UIElementFactory<T> {

    T createElement(Node node, NamedNodeMap attributes);

    UIElementType getType();
}
