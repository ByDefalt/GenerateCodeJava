package xmlio;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import metaModel.*;
import metaModel.types.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import visitor.Visitor;

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
    public void visitEntity(metaModel.Entity e) {
        Element elem = this.doc.createElement("Entity");
        this.addIdToElement(elem);
        String entityId = "#" + this.counter.toString();

        Attr attr = doc.createAttribute("model");
        attr.setValue(modelId);
        elem.setAttributeNode(attr);

        attr = doc.createAttribute("name");
        attr.setValue(e.getName().toString());
        elem.setAttributeNode(attr);


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


        // Sérialiser le type avec la nouvelle hiérarchie
        Type type = a.getType();
        serializeType(type, elem);

        this.root.appendChild(elem);
        elements.add(elem);
    }

    /**
     * Sérialise un type en utilisant la nouvelle hiérarchie OCP
     */
    private void serializeType(Type type, Element elem) {
        Attr attr;

        if (type instanceof SimpleType) {
            // Type simple
            attr = doc.createAttribute("type");
            attr.setValue(type.getBaseType());
            elem.setAttributeNode(attr);

        } else if (type instanceof ArrayType) {
            // Array avec taille
            ArrayType arrayType = (ArrayType) type;

            attr = doc.createAttribute("type");
            attr.setValue("Array");
            elem.setAttributeNode(attr);

            attr = doc.createAttribute("of");
            attr.setValue(arrayType.getElementType());
            elem.setAttributeNode(attr);

            if (arrayType.getSize() != null) {
                attr = doc.createAttribute("size");
                attr.setValue(arrayType.getSize().toString());
                elem.setAttributeNode(attr);
            }

        } else if (type instanceof CollectionType) {
            // List, Set, Bag avec cardinalités
            CollectionType collectionType = (CollectionType) type;

            attr = doc.createAttribute("type");
            attr.setValue(collectionType.getCollectionTypeName());
            elem.setAttributeNode(attr);

            attr = doc.createAttribute("of");
            attr.setValue(collectionType.getElementType());
            elem.setAttributeNode(attr);

            // Ajouter les cardinalités si présentes
            if (collectionType.getMinCardinality() != null) {
                attr = doc.createAttribute("min");
                attr.setValue(collectionType.getMinCardinality().toString());
                elem.setAttributeNode(attr);
            }
            if (collectionType.getMaxCardinality() != null) {
                attr = doc.createAttribute("max");
                attr.setValue(collectionType.getMaxCardinality().toString());
                elem.setAttributeNode(attr);
            }
        }
    }

    @Override
    public void visitAttribute(Attribute e) {
        // Cette méthode ne devrait plus être appelée directement
        // On utilise visitAttributeWithEntityId à la place
    }

    @Override
    public void visitSimpleType(SimpleType e) {

    }

    @Override
    public void visitArrayType(ArrayType e) {

    }

    @Override
    public void visitListType(ListType e) {

    }

    @Override
    public void visitSetType(SetType e) {

    }

    @Override
    public void visitBagType(BagType e) {

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
        for (metaModel.Entity n : e.getEntities()) {
            n.accept(this);
        }
    }
}