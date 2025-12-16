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

    public Document result() {
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
        if (rootId == null || rootId.isEmpty()) {
            Attr attr = this.doc.createAttribute("model");
            attr.setValue("#" + this.counter.toString());
            this.root.setAttributeNode(attr);
            modelId = attr.getValue();
        }
    }

    @Override
    public void visitModel(Model e) {
        Element elem = this.doc.createElement("Model");
        this.addIdToElement(elem);
        this.maybeUpdateRootFrom(elem);

        if (e.getName() != null && !e.getName().isEmpty()) {
            Attr attr = doc.createAttribute("name");
            attr.setValue(e.getName());
            this.root.setAttributeNode(attr); // On met le nom sur la racine pour l'affichage

            // On peut aussi le mettre sur l'élément Model lui-même
            Attr attrModel = doc.createAttribute("name");
            attrModel.setValue(e.getName());
            elem.setAttributeNode(attrModel);
        }

        this.root.appendChild(elem);
        elements.add(elem);

        if (e.getEntities() != null) {
            for (metaModel.Entity n : e.getEntities()) {
                n.accept(this);
            }
        }
    }

    @Override
    public void visitEntity(metaModel.Entity e) {
        Element elem = this.doc.createElement("Entity");
        this.addIdToElement(elem);
        String entityId = elem.getAttribute("id");

        Attr attr = doc.createAttribute("model");
        attr.setValue(modelId);
        elem.setAttributeNode(attr);

        attr = doc.createAttribute("name");
        attr.setValue(e.getName());
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

        // Sérialiser le type
        Type type = a.getType();
        serializeType(type, elem);

        this.root.appendChild(elem);
        elements.add(elem);
    }

    /**
     * Helper pour extraire le nom du type sous forme de String
     * (nécessaire car getElementType retourne un objet Type)
     */
    private String getTypeNameString(Type type) {
        if (type instanceof SimpleType) {
            return ((SimpleType) type).getTypeName();
        } else if (type instanceof ResolvedReference) {
            return ((ResolvedReference) type).getReferencedEntity().getName();
        } else if (type instanceof UnresolvedReference) {
            return ((UnresolvedReference) type).getEntityName();
        } else if (type instanceof CollectionType) {
            // Pour les collections, on veut le type de l'élément contenu
            return getTypeNameString(((CollectionType) type).getElementType());
        }
        return "Unknown";
    }

    /**
     * Sérialise un type en utilisant le méta-modèle fourni
     */
    private void serializeType(Type type, Element elem) {
        Attr attrType = doc.createAttribute("type");

        if (type instanceof SimpleType) {
            // Cas simple : String, Integer, etc.
            attrType.setValue(((SimpleType) type).getTypeName());
            elem.setAttributeNode(attrType);

        } else if (type instanceof ReferenceType) {
            // Cas référence (traitée comme un type simple pointant vers l'entité)
            String refName = (type instanceof ResolvedReference)
                    ? ((ResolvedReference) type).getReferencedEntity().getName()
                    : ((UnresolvedReference) type).getEntityName();

            attrType.setValue(refName);
            elem.setAttributeNode(attrType);

            // Optionnel : marquer que c'est une référence
            Attr attrRef = doc.createAttribute("isReference");
            attrRef.setValue("true");
            elem.setAttributeNode(attrRef);

        } else if (type instanceof CollectionType) {
            // Cas Collections (Array, List, Set, Bag)
            CollectionType collType = (CollectionType) type;

            // 1. Déterminer le nom de la collection pour l'attribut "type"
            String collectionName = "Collection";
            if (type instanceof ArrayType) collectionName = "Array";
            else if (type instanceof ListType) collectionName = "List";
            else if (type instanceof SetType) collectionName = "Set";
            else if (type instanceof BagType) collectionName = "Bag";

            attrType.setValue(collectionName);
            elem.setAttributeNode(attrType);

            // 2. Définir le type contenu ("of")
            Attr attrOf = doc.createAttribute("of");
            attrOf.setValue(getTypeNameString(collType.getElementType()));
            elem.setAttributeNode(attrOf);

            // 3. Gestion spécifique Array (taille)
            if (type instanceof ArrayType) {
                Integer size = ((ArrayType) type).getSize();
                if (size != null) {
                    Attr attrSize = doc.createAttribute("size");
                    attrSize.setValue(size.toString());
                    elem.setAttributeNode(attrSize);
                }
            }

            // 4. Gestion des cardinalités (Min/Max)
            if (collType.getMinCardinality() != null) {
                Attr attrMin = doc.createAttribute("min");
                attrMin.setValue(collType.getMinCardinality().toString());
                elem.setAttributeNode(attrMin);
            }
            if (collType.getMaxCardinality() != null) {
                Attr attrMax = doc.createAttribute("max");
                attrMax.setValue(collType.getMaxCardinality().toString());
                elem.setAttributeNode(attrMax);
            }
        }
    }

    // --- Méthodes Visitor (Laissées vides car on gère la logique via serializeType ou visitAttributeWithEntityId) ---

    @Override
    public void visitAttribute(Attribute e) {
        // Non utilisé directement, voir visitAttributeWithEntityId
    }

    @Override
    public void visitSimpleType(SimpleType e) {}

    @Override
    public void visitArrayType(ArrayType e) {}

    @Override
    public void visitListType(ListType e) {}

    @Override
    public void visitSetType(SetType e) {}

    @Override
    public void visitBagType(BagType e) {}

    @Override
    public void visitResolvedReference(ResolvedReference e) {}

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {}
}