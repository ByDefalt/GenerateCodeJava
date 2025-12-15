package XMLIO;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import metaModel.*;
import metaModel.Entity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {

    // Les clés des 2 Map sont les id

    // Map des instances de la syntaxe abstraite (metamodel)
    protected Map<String, MinispecElement> minispecIndex;
    // Map des elements XML
    protected Map<String, Element> xmlElementIndex;

    public XMLAnalyser() {
        this.minispecIndex = new HashMap<String, MinispecElement>();
        this.xmlElementIndex = new HashMap<String, Element>();
    }

    protected Model modelFromElement(Element e) {
        Model model = new Model();
        String name = e.getAttribute("name");
        if (name != null && !name.isEmpty()) {
            model.setName(name);
        }
        return model;
    }

    protected Entity entityFromElement(Element e) {
        String name = e.getAttribute("name");
        String superType = e.getAttribute("supertype");

        Entity entity = new Entity();
        entity.setName(name);

        if (superType != null && !superType.isEmpty()) {
            entity.setSuperType(superType);
        }

        // Initialiser la liste d'attributs
        entity.setAttributes(new ArrayList<>());

        // Récupérer le modèle parent
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

        // Ajouter l'attribut à l'entité parente
        Element entityElement = this.xmlElementIndex.get(e.getAttribute("entity"));
        if (entityElement != null) {
            Entity entity = (Entity) minispecElementFromXmlElement(entityElement);
            if (entity.getAttributes() != null) {
                entity.getAttributes().add(attribute);
            }
        }

        return attribute;
    }

    /**
     * Parse le type depuis un élément XML Attribute
     * Supporte:
     * - Types simples: <attribute name="nom" type="String"/>
     * - Références: <attribute name="parent" type="Flotte"/>
     * - Collections: <attribute name="satellites" type="List" of="Satellite"/>
     * - Collections avec cardinalités: <attribute name="satellites" type="List" of="Satellite" min="1" max="10"/>
     * - Arrays: <attribute name="panneaux" type="Array" of="PanneauSolaire" size="2"/>
     */
    protected Type parseType(Element e) {
        String typeStr = e.getAttribute("type");
        String ofType = e.getAttribute("of");

        // Type simple ou référence à une entité
        if (ofType == null || ofType.isEmpty()) {
            return new Type(typeStr);
        }

        // C'est une collection
        String collectionType = typeStr; // List, Set, Bag, Array

        // Array avec taille fixe
        if ("Array".equals(collectionType)) {
            String sizeStr = e.getAttribute("size");
            if (sizeStr != null && !sizeStr.isEmpty()) {
                Integer size = Integer.parseInt(sizeStr);
                return new Type(ofType, size);
            }
        }

        // Collection avec cardinalités
        String minStr = e.getAttribute("min");
        String maxStr = e.getAttribute("max");

        if ((minStr != null && !minStr.isEmpty()) || (maxStr != null && !maxStr.isEmpty())) {
            Integer min = (minStr != null && !minStr.isEmpty()) ? Integer.parseInt(minStr) : null;
            Integer max = (maxStr != null && !maxStr.isEmpty()) ? Integer.parseInt(maxStr) : null;
            return new Type(ofType, collectionType, min, max);
        }

        // Collection simple sans cardinalités
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

    // alimentation du map des elements XML
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

    // alimentation du map des instances de la syntaxe abstraite (metamodel)
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

        // Récupérer le nom depuis l'élément Root
        if (model != null) {
            String name = e.getAttribute("name");
            if (name != null && !name.isEmpty()) {
                model.setName(name);
            }
        }

        return model;
    }

    public Model getModelFromInputStream(InputStream stream) {
        try {
            // création d'une fabrique de documents
            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

            // création d'un constructeur de documents
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