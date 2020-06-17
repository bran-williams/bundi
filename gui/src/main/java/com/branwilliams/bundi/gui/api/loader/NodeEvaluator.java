package com.branwilliams.bundi.gui.api.loader;

import org.w3c.dom.Node;

import java.util.Map;

public interface NodeEvaluator {

    /**
     * @return The type of node.
     * */
    String getNodeName();

    Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env, TemplateEvaluator templateEvaluator);
}
