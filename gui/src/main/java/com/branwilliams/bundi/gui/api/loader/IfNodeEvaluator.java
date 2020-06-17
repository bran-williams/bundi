package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.Map;

public class IfNodeEvaluator implements NodeEvaluator {

    private static final String IF_NODE_NAME = "if";

    private static final String IF_VAR_ATTR = "var";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getNodeName() {
        return IF_NODE_NAME;
    }

    @Override
    public Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env,
                                        TemplateEvaluator templateEvaluator) {
        Node variable = original.getAttributes().getNamedItem(IF_VAR_ATTR);
        Object val = env.getOrDefault(variable.getTextContent(), null);

        if (!(val instanceof Boolean)) {
            log.error(variable.getTextContent() + " is not a boolean!");
            return null;
        }

        if ((boolean) val) {
            Node finalParent = parent;
            XmlUtils.forChildren(original, (c) -> templateEvaluator.applyTemplateToDocument(finalParent, c, env));
        }
        return parent;
    }
}
