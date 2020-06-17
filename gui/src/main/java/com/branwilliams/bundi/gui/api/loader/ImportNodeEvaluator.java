package com.branwilliams.bundi.gui.api.loader;

import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.branwilliams.bundi.gui.api.loader.UILoader.UI_BASE_ELEMENT;

public class ImportNodeEvaluator implements NodeEvaluator {

    private static final String IMPORT_NODE_NAME = "import";

    private static final String IMPORT_FILE_ATTR = "file";

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
        Node file = original.getAttributes().getNamedItem(IMPORT_FILE_ATTR);

        if (file == null) {
            log.error("Unable to find attribute for file for env node.");
            return parent;
        }
        String fileName = file.getTextContent();

        String fileContents = IOUtils.readFile(directory, fileName, null);
        if (fileContents == null) {
            log.error("Unable to read file " + directory.resolve(fileName));
            return null;
        }

        try {
            Document document = XmlUtils.fromString(fileContents);
            Node documentElement = document.getDocumentElement();

            if (UI_BASE_ELEMENT.equalsIgnoreCase(documentElement.getNodeName())) {
                XmlUtils.forChildren(documentElement,
                        (c) -> {
                    c = templateEvaluator.getCurrentlyLoadingDocument().importNode(c, true);
                    templateEvaluator.applyTemplateToDocument(parent, c, env);
                });
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("Unable to parse XML from document " + directory.resolve(fileName));
        }

        return parent;
    }
}
