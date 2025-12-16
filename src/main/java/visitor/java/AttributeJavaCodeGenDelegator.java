package visitor.java;

import metaModel.Attribute;
import metaModel.types.CollectionType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

public class AttributeJavaCodeGenDelegator implements CodeGenDelegator {

    private static final String FIELD_TEMPLATE =
            "    private $Type $VarName;\n";

    private static final String GETTER_TEMPLATE =
            "\n    public $Type get$CapVarName() {\n" +
                    "        return $VarName;\n" +
                    "    }\n";

    private static final String SETTER_TEMPLATE =
            "\n    public void set$CapVarName($Type $VarName) {\n" +
                    "        this.$VarName = $VarName;\n" +
                    "    }\n";

    @Override
    public void delegate(Object element, Visitor visitor) {
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();
        Attribute attr = (Attribute) element;

        attr.getType().accept(visitor);
        String type = ctx.currentType;
        String name = attr.getName();
        String cap = capitalize(name);

        // Génération du champ
        ctx.fields.append(
                FIELD_TEMPLATE
                        .replace("$Type", type)
                        .replace("$VarName", name)
        );

        // Génération getter
        ctx.methods.append(
                GETTER_TEMPLATE
                        .replace("$Type", type)
                        .replace("$VarName", name)
                        .replace("$CapVarName", cap)
        );

        // Génération setter
        ctx.methods.append(
                SETTER_TEMPLATE
                        .replace("$Type", type)
                        .replace("$VarName", name)
                        .replace("$CapVarName", cap)
        );

        // Collection
        if (attr.getType() instanceof CollectionType) {
            v.getCollectionDelegator().delegate(attr, visitor);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
