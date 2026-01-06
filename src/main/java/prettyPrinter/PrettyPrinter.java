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
        currentTypeString = "";
        e.getType().accept(this);
        result = result + "    " + e.getName() + " : " + currentTypeString;
        if(e.getInitialValue()!=null) {
             result = result + " := " + e.getInitialValue();
        }
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

        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitListType(ListType e) {
        StringBuilder sb = new StringBuilder("List");

        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitSetType(SetType e) {
        StringBuilder sb = new StringBuilder("Set");

        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }

    @Override
    public void visitBagType(BagType e) {
        StringBuilder sb = new StringBuilder("Bag");

        if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
            sb.append(" [");
            sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
            sb.append(":");
            sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
            sb.append("]");
        }

        String elementTypeString = getElementTypeString(e);

        sb.append(" of ").append(elementTypeString);
        currentTypeString = sb.toString();
    }


    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        currentTypeString = e.getEntityName();
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        currentTypeString = e.getEntityName();
    }

    private String getElementTypeString(CollectionType e) {
        String savedCurrentTypeString = currentTypeString;

        e.getElementType().accept(this);
        String elementTypeString = currentTypeString;

        currentTypeString = savedCurrentTypeString;

        return elementTypeString;
    }
}