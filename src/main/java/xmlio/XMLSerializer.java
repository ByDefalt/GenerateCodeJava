package xmlio;

import metaModel.minispec.*;
import metaModel.minispec.types.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * XMLSerializer - Sérialise un modèle MiniSpec en XML
 *
 * Fonctionnalités:
 * - Sérialisation complète d'un modèle avec ses entités et attributs
 * - Génération d'IDs uniques pour chaque élément
 * - Support de tous les types (simples, références, collections)
 * - Export vers String, File, ou OutputStream
 */
public class XMLSerializer {

    private Document document;
    private Map<Object, String> idMap;
    private int idCounter;

    public XMLSerializer() {
        this.idMap = new HashMap<>();
        this.idCounter = 1;
    }

    /**
     * Sérialise un modèle en XML et retourne une chaîne
     */
    public String serializeToString(Model model) {
        try {
            StringWriter writer = new StringWriter();
            serialize(model, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sérialise un modèle en XML dans un fichier
     */
    public boolean serializeToFile(Model model, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            serialize(model, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sérialise un modèle en XML dans un fichier (par nom)
     */
    public boolean serializeToFile(Model model, String filename) {
        return serializeToFile(model, new File(filename));
    }

    /**
     * Sérialise un modèle en XML dans un OutputStream
     */
    public void serialize(Model model, OutputStream outputStream) throws ParserConfigurationException, TransformerException {
        Writer writer = new OutputStreamWriter(outputStream);
        serialize(model, writer);
    }

    /**
     * Méthode principale de sérialisation
     */
    private void serialize(Model model, Writer writer) throws ParserConfigurationException, TransformerException {
        // Réinitialiser l'état
        idMap.clear();
        idCounter = 1;

        // Créer le document XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        // Créer l'élément racine
        Element root = document.createElement("Root");
        document.appendChild(root);

        // Sérialiser le modèle
        String modelId = generateId(model);
        root.setAttribute("model", modelId);

        Element modelElement = serializeModel(model);
        root.appendChild(modelElement);

        // Sérialiser toutes les entités
        for (Entity entity : model.getEntities()) {
            serializeEntityRecursive(entity, root, modelId);
        }

        // Écrire le document
        writeDocument(writer);
    }

    /**
     * Sérialise le modèle
     */
    private Element serializeModel(Model model) {
        Element element = document.createElement("Model");
        String id = getId(model);
        element.setAttribute("id", id);

        if (model.getName() != null && !model.getName().isEmpty()) {
            element.setAttribute("name", model.getName());
        }

        return element;
    }

    /**
     * Sérialise une entité et tous ses attributs
     */
    private void serializeEntityRecursive(Entity entity, Element parent, String modelId) {
        // Créer l'élément Entity
        Element entityElement = document.createElement("Entity");
        String entityId = generateId(entity);
        entityElement.setAttribute("id", entityId);
        entityElement.setAttribute("model", modelId);
        entityElement.setAttribute("name", entity.getName());

        // Gérer l'héritage
        if (entity.getSuperEntity() != null) {
            String superEntityId = getId(entity.getSuperEntity());
            entityElement.setAttribute("extend", superEntityId);
        }

        parent.appendChild(entityElement);

        // Sérialiser les attributs
        if (entity.getAttributes() != null) {
            for (Attribute attribute : entity.getAttributes()) {
                serializeAttribute(attribute, parent, entityId);
            }
        }
    }

    /**
     * Sérialise un attribut
     */
    private void serializeAttribute(Attribute attribute, Element parent, String entityId) {
        Element attrElement = document.createElement("Attribute");
        String attrId = generateId(attribute);
        attrElement.setAttribute("id", attrId);
        attrElement.setAttribute("entity", entityId);
        attrElement.setAttribute("name", attribute.getName());

        // Gérer le type
        Type type = attribute.getType();
        String typeRef = serializeType(type, parent);
        attrElement.setAttribute("type", typeRef);

        // Gérer la valeur initiale
        if (attribute.getInitialValue() != null && !attribute.getInitialValue().isEmpty()) {
            attrElement.setAttribute("init", attribute.getInitialValue());
        }

        parent.appendChild(attrElement);
    }

    /**
     * Sérialise un type et retourne sa référence
     */
    private String serializeType(Type type, Element parent) {
        if (type instanceof SimpleType) {
            return ((SimpleType) type).getTypeName();
        } else if (type instanceof UnresolvedReference) {
            return ((UnresolvedReference) type).getEntityName();
        } else if (type instanceof ResolvedReference) {
            ResolvedReference ref = (ResolvedReference) type;
            Element refElement = document.createElement("Reference");
            String refId = generateId(ref);
            refElement.setAttribute("id", refId);
            refElement.setAttribute("name", ref.getEntityName());
            refElement.setAttribute("entity", getId(ref.getReferencedEntity()));
            parent.appendChild(refElement);
            return refId;
        } else if (type instanceof CollectionType) {
            return serializeCollectionType((CollectionType) type, parent);
        }

        return "Unknown";
    }

    /**
     * Sérialise un type collection
     */
    private String serializeCollectionType(CollectionType collectionType, Element parent) {
        Element collElement;
        String collId = generateId(collectionType);

        if (collectionType instanceof ArrayType) {
            collElement = document.createElement("Array");
            ArrayType arrayType = (ArrayType) collectionType;
            if (arrayType.getSize() != null) {
                collElement.setAttribute("size", arrayType.getSize().toString());
            }
        } else if (collectionType instanceof ListType) {
            collElement = document.createElement("List");
            ListType listType = (ListType) collectionType;
            addCardinalityAttributes(collElement, listType);
        } else if (collectionType instanceof SetType) {
            collElement = document.createElement("Set");
            SetType setType = (SetType) collectionType;
            addCardinalityAttributes(collElement, setType);
        } else if (collectionType instanceof BagType) {
            collElement = document.createElement("Bag");
            BagType bagType = (BagType) collectionType;
            addCardinalityAttributes(collElement, bagType);
        } else {
            return "UnknownCollection";
        }

        collElement.setAttribute("id", collId);

        // Gérer le type d'élément
        Type elementType = collectionType.getElementType();
        if (elementType instanceof ResolvedReference) {
            ResolvedReference ref = (ResolvedReference) elementType;
            // Créer une référence séparée pour l'élément
            Element refElement = document.createElement("Reference");
            String refId = generateId(ref);
            refElement.setAttribute("id", refId);
            refElement.setAttribute("name", ref.getEntityName());
            refElement.setAttribute("entity", getId(ref.getReferencedEntity()));
            parent.appendChild(refElement);
            collElement.setAttribute("ref", refId);
        } else if (elementType instanceof SimpleType) {
            collElement.setAttribute("of", ((SimpleType) elementType).getTypeName());
        } else if (elementType instanceof UnresolvedReference) {
            collElement.setAttribute("of", ((UnresolvedReference) elementType).getEntityName());
        }

        parent.appendChild(collElement);
        return collId;
    }

    /**
     * Ajoute les attributs de cardinalité à un élément collection
     */
    private void addCardinalityAttributes(Element element, CollectionType collectionType) {
        if (collectionType.getMinCardinality() != null) {
            element.setAttribute("min", collectionType.getMinCardinality().toString());
        }
        if (collectionType.getMaxCardinality() != null) {
            element.setAttribute("max", collectionType.getMaxCardinality().toString());
        } else {
            element.setAttribute("max", "*");
        }
    }

    /**
     * Génère un nouvel ID pour un objet
     */
    private String generateId(Object obj) {
        if (!idMap.containsKey(obj)) {
            String id = "#" + idCounter++;
            idMap.put(obj, id);
            return id;
        }
        return idMap.get(obj);
    }

    /**
     * Récupère l'ID d'un objet déjà sérialisé
     */
    private String getId(Object obj) {
        if (idMap.containsKey(obj)) {
            return idMap.get(obj);
        }
        return generateId(obj);
    }

    /**
     * Écrit le document XML dans un Writer
     */
    private void writeDocument(Writer writer) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
    }

}