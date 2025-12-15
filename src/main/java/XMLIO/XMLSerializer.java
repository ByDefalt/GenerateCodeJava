package XMLIO;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import metaModel.Attribute;
import metaModel.Type;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class XMLSerializer extends Visitor {
    List<Element> elements;
    Element root = null;
    String modelId;
    Integer counter;
    Document doc;

    Document result() {
        return this.doc;
    }

    public XMLSerializer() throws ParserConfigurationException {
        this.elements = new ArrayList<>();
        this.counter = 0;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.newDocument();
        root = this.doc.createElement("Root");
        this.doc.appendChild(root);
    }

    private void addIdToElement(Element e) {
        this.counter++;
        Attr attr = this.doc.createAttribute("id");
        attr.setValue("#" + this.counter.toString());
        e.setAttributeNode(attr);
    }

    private void maybeUpdateRootFrom(Element e) {
        String rootId = this.root.getAttribute("model");
        if (rootId.isEmpty()) {
            Attr attr = this.doc.createAttribute("model");
            attr.setValue("#" + this.counter.toString());
            this.root.setAttributeNode(attr);
            modelId = attr.getValue();
        }
    }

    @Override
    public void visitEntity(Entity e) {
        Element elem = this.doc.createElement("Entity");
        this.addIdToElement(elem);
        String entityId = "#" + this.counter.toString();

        Attr attr = doc.createAttribute("model");
        attr.setValue(modelId);
        elem.setAttributeNode(attr);

        attr = doc.createAttribute("name");
        attr.setValue(e.getName().toString());
        elem.setAttributeNode(attr);

        // Ajouter l'attribut supertype si présent
        if (e.hasSuperType()) {
            attr = doc.createAttribute("supertype");
            attr.setValue(e.getSuperType());
            elem.setAttributeNode(attr);
        }

        this.root.appendChild(elem);
        elements.add(elem);

        // Visiter les attributs
        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                visitAttributeWithEntityId(a, entityId);
            }
        }
    }

    private void visitAttributeWithEntityId(Attribute a, String entityId) {
        Element elem = this.doc.createElement("Attribute");
        this.addIdToElement(elem);

        Attr attr = doc.createAttribute("entity");
        attr.setValue(entityId);
        elem.setAttributeNode(attr);

        attr = doc.createAttribute("name");
        attr.setValue(a.getName());
        elem.setAttributeNode(attr);

        // Ajouter la valeur initiale si présente
        if (a.hasInitialValue()) {
            attr = doc.createAttribute("init");
            attr.setValue(a.getInitialValue());
            elem.setAttributeNode(attr);
        }

        // Sérialiser le type
        Type type = a.getType();
        if (!type.isCollection()) {
            // Type simple
            attr = doc.createAttribute("type");
            attr.setValue(type.getBaseType());
            elem.setAttributeNode(attr);
        } else {
            // Collection
            attr = doc.createAttribute("type");
            attr.setValue(type.getCollectionType());
            elem.setAttributeNode(attr);

            attr = doc.createAttribute("of");
            attr.setValue(type.getBaseType());
            elem.setAttributeNode(attr);

            // Ajouter cardinalités ou taille
            if ("Array".equals(type.getCollectionType()) && type.getArraySize() != null) {
                attr = doc.createAttribute("size");
                attr.setValue(type.getArraySize().toString());
                elem.setAttributeNode(attr);
            } else {
                if (type.getMinCardinality() != null) {
                    attr = doc.createAttribute("min");
                    attr.setValue(type.getMinCardinality().toString());
                    elem.setAttributeNode(attr);
                }
                if (type.getMaxCardinality() != null) {
                    attr = doc.createAttribute("max");
                    attr.setValue(type.getMaxCardinality().toString());
                    elem.setAttributeNode(attr);
                }
            }
        }

        this.root.appendChild(elem);
        elements.add(elem);
    }

    @Override
    public void visitAttribute(Attribute e) {
        // Cette méthode ne devrait plus être appelée directement
        // On utilise visitAttributeWithEntityId à la place
    }

    @Override
    public void visitType(Type e) {
        // Le type est sérialisé dans visitAttributeWithEntityId
        // Cette méthode est présente pour satisfaire l'interface Visitor
    }

    @Override
    public void visitModel(Model e) {
        Element elem = this.doc.createElement("Model");
        this.addIdToElement(elem);
        this.maybeUpdateRootFrom(elem);

        // Ajouter l'attribut name sur Root si le modèle a un nom
        if (e.getName() != null && !e.getName().isEmpty()) {
            Attr attr = doc.createAttribute("name");
            attr.setValue(e.getName());
            this.root.setAttributeNode(attr);
        }

        this.root.appendChild(elem);
        elements.add(elem);
        for (Entity n : e.getEntities()) {
            n.accept(this);
        }
    }
}