package prettyPrinter;

import metaModel.*;
import metaModel.types.*;
import visitor.Visitor;

/**
 * PrettyPrinter pour la syntaxe Minispec
 * Utilise le pattern Visitor pour séparer la logique de présentation du métamodèle
 */
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

    @Override
    public void visitArrayType(ArrayType e) {
        StringBuilder sb = new StringBuilder("Array");

        if (e.getSize() != null) {
            sb.append(" [").append(e.getSize()).append("]");
        }

        sb.append(" of ").append(e.getElementType());
        currentTypeString = sb.toString();
    }

    @Override
    public void visitListType(ListType e) {
        StringBuilder sb = new StringBuilder("List");

        if (e.hasCardinality()) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        sb.append(" of ").append(e.getElementType());
        currentTypeString = sb.toString();
    }

    @Override
    public void visitSetType(SetType e) {
        StringBuilder sb = new StringBuilder("Set");

        if (e.hasCardinality()) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        sb.append(" of ").append(e.getElementType());
        currentTypeString = sb.toString();
    }

    @Override
    public void visitBagType(BagType e) {
        StringBuilder sb = new StringBuilder("Bag");

        if (e.hasCardinality()) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        sb.append(" of ").append(e.getElementType());
        currentTypeString = sb.toString();
    }
}