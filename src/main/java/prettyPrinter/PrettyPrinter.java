package prettyPrinter;

import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import metaModel.minispec.types.*;
import visitor.Visitor;


public class PrettyPrinter extends Visitor {
    private String result = "";
    private String currentTypeString = "";

    public String result() {
        return result;
    }

    @Override
    public void visitModel(Model e) {
        if (e.getName() != null && !e.getName().isEmpty()) {
            result = "model " + e.getName() + " ;\n\n";
        } else {
            result = "model ;\n\n";
        }

        if (e.getEntities() != null) {
            for (Entity n : e.getEntities()) {
                n.accept(this);
            }
        }

        result = result + "end model\n";
    }

    @Override
    public void visitEntity(Entity e) {
        result = result + "entity " + e.getName();
        if (e.getSuperEntity() != null) {
            result = result + " subtype of (" + e.getSuperEntity().getName() + ")";
        }

        result = result + " ;\n";

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                a.accept(this);
            }
        }

        result = result + "end entity;\n\n";
    }

    @Override
    public void visitAttribute(Attribute e) {
        // Visiter le type pour obtenir sa représentation
        currentTypeString = "";
        e.getType().accept(this);

        result = result + "    " + e.getName() + " : " + currentTypeString;

        result = result + " ;\n";
    }

    @Override
    public void visitSimpleType(SimpleType e) {
        currentTypeString = e.getTypeName();
    }

    // --- Types de Collection ---

    @Override
    public void visitArrayType(ArrayType e) {
        StringBuilder sb = new StringBuilder("Array");

        if (e.getSize() != null) {
            sb.append(" [").append(e.getSize()).append("]");
        }

        // Visiter récursivement le type d'élément
        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitListType(ListType e) {
        StringBuilder sb = new StringBuilder("List");

        // Utiliser getMinCardinality() et getMaxCardinality()
        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        // Visiter récursivement le type d'élément
        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitSetType(SetType e) {
        StringBuilder sb = new StringBuilder("Set");

        // Utiliser getMinCardinality() et getMaxCardinality()
        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        // Visiter récursivement le type d'élément
        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitBagType(BagType e) {
        StringBuilder sb = new StringBuilder("Bag");

        // Utiliser getMinCardinality() et getMaxCardinality()
        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        // Visiter récursivement le type d'élément
        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    // --- Types de Référence ---

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        // Une référence non résolue est affichée comme son nom d'entité
        currentTypeString = e.getEntityName();
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        // Une référence résolue est affichée comme son nom d'entité (on ignore l'objet Entity pour la pretty-impression)
        currentTypeString = e.getEntityName();
    }

    // --- Méthode d'aide pour la visite récursive des types d'élément ---
    private String getElementTypeString(CollectionType e) {
        // Sauvegarder l'état actuel pour la récursion
        String savedCurrentTypeString = currentTypeString;

        // Visiter le type d'élément
        e.getElementType().accept(this);
        String elementTypeString = currentTypeString;

        // Restaurer l'état pour la suite de la méthode appelante
        currentTypeString = savedCurrentTypeString;

        return elementTypeString;
    }
}