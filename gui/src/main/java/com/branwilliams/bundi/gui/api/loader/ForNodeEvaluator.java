package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ForNodeEvaluator implements NodeEvaluator {

    private static final String FOR_NODE_NAME = "for";

    private static final String FOR_LIST_ATTR = "list";

    private static final String FOR_VAR_ATTR = "var";

    private static final String FOR_INDEX_ATTR = "index";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getNodeName() {
        return FOR_NODE_NAME;
    }

    @Override
    public Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env,
                                        TemplateEvaluator templateEvaluator) {
        Node list = original.getAttributes().getNamedItem(FOR_LIST_ATTR);
        Node var = original.getAttributes().getNamedItem(FOR_VAR_ATTR);
        Node index = original.getAttributes().getNamedItem(FOR_INDEX_ATTR);

        if (list == null || var == null) {
            log.error("Unable to find attribute for 'list' and/or 'var' in for node");
            return parent;
        }

        Object iter = env.getOrDefault(list.getTextContent(), null);

        int iterIndex = 0;
        if (iter instanceof Iterable) {
            Iterable<?> iterable = (Iterable) iter;
            Iterator<?> iterator = iterable.iterator();

            Node finalParent = parent;
            while (iterator.hasNext()) {
                Object next = iterator.next();

                Map<String, Object> forEnv = new HashMap<>(env);
                forEnv.put(var.getTextContent(), next);
                if (index != null) {
                    forEnv.put(index.getTextContent(), iterIndex);
                }

                XmlUtils.forChildren(original, (c) -> templateEvaluator.applyTemplateToDocument(finalParent, c, forEnv));

                iterIndex++;
            }
        } else {
            log.error("Environment variable '" + list.getTextContent() + "' is not iterable.");
        }
        return parent;
    }
}
