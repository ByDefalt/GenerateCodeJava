package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.types.Type;
import visitor.Visitor;

public class PrettyPrinter extends Visitor {
    String result = "";
    String currentTypeString = "";

    public String result() {
        return result;
    }

    public void visitModel(Model e) {
        result = "model "+e.getName()+";\n\n";

        for (Entity n : e.getEntities()) {
            n.accept(this);
        }
        result = result + "end model;\n";
    }

    public void visitEntity(Entity e) {
        result = result + "entity " + e.getName() + " ;\n";
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

        result = result + "    " + e.getName() + " : " + currentTypeString + " ;\n";
    }

    @Override
    public void visitType(Type e) {
        if (!e.isCollection()) {
            currentTypeString = e.getBaseType();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getCollectionType());

            if ("Array".equals(e.getCollectionType()) && e.getArraySize() != null) {
                sb.append(" [").append(e.getArraySize()).append("]");
            } else if (e.getMinCardinality() != null || e.getMaxCardinality() != null) {
                sb.append(" [");
                sb.append(e.getMinCardinality() != null ? e.getMinCardinality() : "*");
                sb.append(":");
                sb.append(e.getMaxCardinality() != null ? e.getMaxCardinality() : "*");
                sb.append("]");
            }

            sb.append(" of ").append(e.getBaseType());
            currentTypeString = sb.toString();
        }
    }
}