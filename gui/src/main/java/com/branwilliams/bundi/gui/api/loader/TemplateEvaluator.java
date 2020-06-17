package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEvaluator {

    /**
     * Group 1 matches: "{{ variable name }}" for environment variables given to this loader.
     * */
    private final Pattern environmentVariablePattern = Pattern.compile("\\{\\{\\s*([\\w\\d]+[^\\s]*)\\s*}}");

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, NodeEvaluator> evaluators = new HashMap<>();

    private Document currentlyLoadingDocument;

    public TemplateEvaluator() {
    }

    public static TemplateEvaluator getDefault(Path directory) {
        TemplateEvaluator templateEvaluator = new TemplateEvaluator();

        templateEvaluator.addNodeEvaluator(new VarSetNodeEvaluator());

        if (directory != null)
            templateEvaluator.addNodeEvaluator(new ImportNodeEvaluator(directory));

        templateEvaluator.addNodeEvaluator(new IfNodeEvaluator());
        templateEvaluator.addNodeEvaluator(new ElseNodeEvaluator());
        templateEvaluator.addNodeEvaluator(new ForNodeEvaluator());

        return templateEvaluator;
    }

    private void addNodeEvaluator(NodeEvaluator evaluator) {
        if (evaluators.containsKey(evaluator.getNodeName())) {
            log.error("Unable to override evaluator for node type \"" + evaluator.getNodeName() + "\"");
        } else {
            evaluators.put(evaluator.getNodeName(), evaluator);
        }
    }

    public Node applyTemplateToDocument(Document document, Node parent, Node original, Map<String, Object> env) {
        this.currentlyLoadingDocument = document;
        Node applied = applyTemplateToDocument(parent, original, env);
        this.currentlyLoadingDocument = null;
        return applied;
    }

    public Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env) {
        String nodeName = original.getNodeName().toLowerCase();
        if (evaluators.containsKey(nodeName)) {
            NodeEvaluator evaluator = evaluators.get(nodeName);
            evaluator.applyTemplateToDocument(parent, original, env, this);
        } else {
            Node copy = original.cloneNode(false);
            XmlUtils.forAttributes(copy, (attr) -> {
                attr.setNodeValue(replaceEnvironmentVariables(attr.getNodeValue(), env));
            });

            if (parent == null) {
                parent = copy;
            } else {
                parent.appendChild(copy);
            }
            XmlUtils.forChildren(original, (c) -> applyTemplateToDocument(copy, c, env));
        }
        return parent;
    }

    private String replaceEnvironmentVariables(String input, Map<String, Object> env) {
        Matcher matcher = environmentVariablePattern.matcher(input);
        return matcher.replaceAll((result) -> env.getOrDefault(result.group(1), "null").toString());
    }

    public Document getCurrentlyLoadingDocument() {
        return currentlyLoadingDocument;
    }
}
