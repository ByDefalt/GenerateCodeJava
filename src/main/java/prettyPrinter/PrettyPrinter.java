package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Type;
import metaModel.Visitor;

public class PrettyPrinter extends Visitor {
    private String result = "";
    private String currentTypeString = ""; // Pour stocker temporairement le résultat de visitType

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

        // Ajouter le supertype si présent
        if (e.hasSuperType()) {
            result = result + " subtype of (" + e.getSuperType() + ")";
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

        result = result + "    " + e.getName() + " : " + currentTypeString + " ;\n";
    }

    @Override
    public void visitType(Type e) {
        if (!e.isCollection()) {
            // Type simple
            currentTypeString = e.getBaseType();
        } else {
            // Collection
            StringBuilder sb = new StringBuilder();
            sb.append(e.getCollectionType());

            // Ajouter les cardinalités ou la taille
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