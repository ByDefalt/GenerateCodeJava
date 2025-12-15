package codeGenConfig;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Parser pour les fichiers de configuration XML de génération de code
 */
public class CodeGenConfigParser {

    public CodeGenConfig parseFromFile(String filename) throws Exception {
        File file = new File(filename);
        return parseFromFile(file);
    }

    public CodeGenConfig parseFromFile(File file) throws Exception {
        try (FileInputStream stream = new FileInputStream(file)) {
            return parseFromInputStream(stream);
        }
    }

    public CodeGenConfig parseFromString(String xmlContent) throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlContent.getBytes());
        return parseFromInputStream(stream);
    }

    public CodeGenConfig parseFromInputStream(InputStream stream) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);

            return parseFromDocument(document);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new Exception("Error parsing code generation configuration: " + e.getMessage(), e);
        }
    }

    private CodeGenConfig parseFromDocument(Document document) {
        CodeGenConfig config = new CodeGenConfig();

        Element root = document.getDocumentElement();

        // Parser les mappings de modèles
        NodeList modelNodes = root.getElementsByTagName("model");
        for (int i = 0; i < modelNodes.getLength(); i++) {
            Element modelElement = (Element) modelNodes.item(i);
            String name = modelElement.getAttribute("name");
            String packageName = modelElement.getAttribute("package");

            if (name != null && !name.isEmpty() && packageName != null && !packageName.isEmpty()) {
                config.addModelMapping(new ModelMapping(name, packageName));
            }
        }

        // Parser les mappings de primitives
        NodeList primitiveNodes = root.getElementsByTagName("primitive");
        for (int i = 0; i < primitiveNodes.getLength(); i++) {
            Element primitiveElement = (Element) primitiveNodes.item(i);
            String name = primitiveElement.getAttribute("name");
            String type = primitiveElement.getAttribute("type");
            String packageName = primitiveElement.getAttribute("package");

            if (name != null && !name.isEmpty() && type != null && !type.isEmpty()) {
                if (packageName != null && !packageName.isEmpty()) {
                    config.addPrimitiveMapping(new PrimitiveMapping(name, type, packageName));
                } else {
                    config.addPrimitiveMapping(new PrimitiveMapping(name, type));
                }
            }
        }

        return config;
    }
}