package xmlio;

import javax.xml.parsers.*;

import metaModel.types.Type;
import org.w3c.dom.*;
import org.xml.sax.*;

import metaModel.*;
import metaModel.Entity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {

    protected Map<String, MinispecElement> minispecIndex;
    protected Map<String, Element> xmlElementIndex;

    public XMLAnalyser() {
        this.minispecIndex = new HashMap<String, MinispecElement>();
        this.xmlElementIndex = new HashMap<String, Element>();
    }

    protected Model modelFromElement(Element e) {
        return new Model(e.getAttribute("name"));
    }

    protected Entity entityFromElement(Element e) {
        String name = e.getAttribute("name");
        Entity entity = new Entity();
        entity.setName(name);

        entity.setAttributes(new ArrayList<>());

        Element modelElement = this.xmlElementIndex.get(e.getAttribute("model"));
        if (modelElement != null) {
            Model model = (Model) minispecElementFromXmlElement(modelElement);
            model.addEntity(entity);
        }

        return entity;
    }

    protected Attribute attributeFromElement(Element e) {
        String name = e.getAttribute("name");
        Type type = parseType(e);
        Attribute attribute = new Attribute(name, type);

        Element entityElement = this.xmlElementIndex.get(e.getAttribute("entity"));
        if (entityElement != null) {
            Entity entity = (Entity) minispecElementFromXmlElement(entityElement);
            if (entity.getAttributes() != null) {
                entity.getAttributes().add(attribute);
            }
        }

        return attribute;
    }

    protected Type parseType(Element e) {
        String typeStr = e.getAttribute("type");
        String ofType = e.getAttribute("of");

        if (ofType == null || ofType.isEmpty()) {
            return new Type(typeStr);
        }

        String collectionType = typeStr;

        if ("Array".equals(collectionType)) {
            String sizeStr = e.getAttribute("size");
            if (sizeStr != null && !sizeStr.isEmpty()) {
                Integer size = Integer.parseInt(sizeStr);
                return new Type(ofType, size);
            }
        }

        String minStr = e.getAttribute("min");
        String maxStr = e.getAttribute("max");

        if ((minStr != null && !minStr.isEmpty()) || (maxStr != null && !maxStr.isEmpty())) {
            Integer min = (minStr != null && !minStr.isEmpty()) ? Integer.parseInt(minStr) : null;
            Integer max = (maxStr != null && !maxStr.isEmpty()) ? Integer.parseInt(maxStr) : null;
            return new Type(ofType, collectionType, min, max);
        }

        return new Type(ofType, collectionType);
    }

    protected MinispecElement minispecElementFromXmlElement(Element e) {
        if (e == null) {
            return null;
        }

        String id = e.getAttribute("id");
        MinispecElement result = this.minispecIndex.get(id);
        if (result != null) return result;

        String tag = e.getTagName();
        if (tag.equals("Model")) {
            result = modelFromElement(e);
        } else if (tag.equals("Entity")) {
            result = entityFromElement(e);
        } else if (tag.equals("Attribute")) {
            result = attributeFromElement(e);
        }

        if (result != null) {
            this.minispecIndex.put(id, result);
        }
        return result;
    }

    protected void firstRound(Element el) {
        NodeList nodes = el.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n instanceof Element) {
                Element child = (Element) n;
                String id = child.getAttribute("id");
                this.xmlElementIndex.put(id, child);
            }
        }
    }

    protected void secondRound(Element el) {
        NodeList nodes = el.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n instanceof Element) {
                minispecElementFromXmlElement((Element)n);
            }
        }
    }

    public Model getModelFromDocument(Document document) {
        Element e = document.getDocumentElement();

        firstRound(e);

        secondRound(e);

        Model model = (Model) this.minispecIndex.get(e.getAttribute("model"));

        return model;
    }

    public Model getModelFromInputStream(InputStream stream) {
        try {
            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            Document document = constructeur.parse(stream);
            return getModelFromDocument(document);

        } catch (ParserConfigurationException pce) {
            System.out.println("Erreur de configuration du parseur DOM");
            System.out.println("lors de l'appel à fabrique.newDocumentBuilder();");
        } catch (SAXException se) {
            System.out.println("Erreur lors du parsing du document");
            System.out.println("lors de l'appel à construteur.parse(xml)");
        } catch (IOException ioe) {
            System.out.println("Erreur d'entrée/sortie");
            System.out.println("lors de l'appel à construteur.parse(xml)");
        }
        return null;
    }

    public Model getModelFromString(String contents) {
        InputStream stream = new ByteArrayInputStream(contents.getBytes());
        return getModelFromInputStream(stream);
    }

    public Model getModelFromFile(File file) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getModelFromInputStream(stream);
    }

    public Model getModelFromFilenamed(String filename) {
        File file = new File(filename);
        return getModelFromFile(file);
    }
}