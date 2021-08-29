package com.branwilliams.bundi.gui.api.loader.template.evaluators;

import com.branwilliams.bundi.gui.api.loader.template.NodeEvaluator;
import com.branwilliams.bundi.gui.api.loader.template.TemplateEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.Map;

public class VarSetNodeEvaluator implements NodeEvaluator {

    private static final String ENV_SET_NODE_NAME = "varset";

    private static final String ENV_SET_VAR_ATTR = "var";

    private static final String ENV_SET_VAL_ATTR = "val";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getNodeName() {
        return ENV_SET_NODE_NAME;
    }

    @Override
    public Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env,
                                        TemplateEvaluator templateEvaluator) {
        Node variable = original.getAttributes().getNamedItem(ENV_SET_VAR_ATTR);
        Node value = original.getAttributes().getNamedItem(ENV_SET_VAL_ATTR);
        if (variable == null || value == null) {
            log.error("Unable to find attributes var or val for VarSet node.");
            return parent;
        }

        env.put(variable.getTextContent().toLowerCase(), value.getTextContent());

        return parent;
    }
}
