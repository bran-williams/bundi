package com.branwilliams.bundi.gui.api.loader.template.evaluators;

import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.gui.api.loader.template.NodeEvaluator;
import com.branwilliams.bundi.gui.api.loader.template.TemplateEvaluator;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.branwilliams.bundi.gui.api.loader.UILoader.UI_BASE_ELEMENT;

public class ImportNodeEvaluator implements NodeEvaluator {

    private static final String IMPORT_NODE_NAME = "import";

    private static final String IMPORT_FILE_ATTR = "file";

    private static final String IMPORT_RES_ATTR = "res";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Path directory;

    public ImportNodeEvaluator(Path directory) {
        this.directory = directory;
    }

    @Override
    public String getNodeName() {
        return IMPORT_NODE_NAME;
    }

    @Override
    public Node applyTemplateToDocument(Node parent, Node original, Map<String, Object> env,
                                        TemplateEvaluator templateEvaluator) {

        String fileContents = readFile(original);
        if (fileContents == null) {
            fileContents = readResource(original);
        }

        if (fileContents == null) {
            log.error("Unable to find " + IMPORT_FILE_ATTR + " or " + IMPORT_RES_ATTR + " in " + IMPORT_NODE_NAME);
            return null;
        }

        try {
            Document document = XmlUtils.fromString(fileContents);
            Node documentElement = document.getDocumentElement();

            if (UI_BASE_ELEMENT.equalsIgnoreCase(documentElement.getNodeName())) {
                Map<String, Object> copyOfEnv = copyEnvWithAttributes(original, env);
                XmlUtils.forChildren(documentElement,
                        (c) -> {
                    c = templateEvaluator.getCurrentlyLoadingDocument().importNode(c, true);
                    templateEvaluator.applyTemplateToDocument(parent, c, copyOfEnv);
                });
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("Unable to parse XML from document " + fileContents);
        }

        return parent;
    }

    private Map<String, Object> copyEnvWithAttributes(Node original, Map<String, Object> env) {
        Map<String, Object> envCopy = new HashMap<>(env);
        for (int i = 0; i < original.getAttributes().getLength(); i++) {
            Node attribute = original.getAttributes().item(i);
            if (IMPORT_FILE_ATTR.equals(attribute.getNodeName()) || IMPORT_RES_ATTR.equals(attribute.getNodeName())) {
                continue;
            }
            envCopy.put(attribute.getNodeName(), attribute.getTextContent());
        }
        return envCopy;
    }


    private String readFile(Node original) {
        Node file = original.getAttributes().getNamedItem(IMPORT_FILE_ATTR);
        if (file == null) {
            return null;
        }
        String fileName = file.getTextContent();

        String fileContents = IOUtils.readFile(directory, fileName, null);
        if (fileContents == null) {
            log.error("Unable to read file " + directory.resolve(fileName));
            return null;
        }
        return fileContents;
    }

    private String readResource(Node original) {
        Node res = original.getAttributes().getNamedItem(IMPORT_RES_ATTR);
        if (res == null) {
            return null;
        }
        String resourceName = res.getTextContent();

        String resContents = IOUtils.readResource(resourceName, null);
        if (resContents == null) {
            log.error("Unable to read resource " + resourceName);
            return null;
        }
        return resContents;
    }

}
