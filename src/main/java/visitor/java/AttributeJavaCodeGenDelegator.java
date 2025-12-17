package visitor.java;

import metaModel.configMetaModel.java.ModelConfig;
import metaModel.configMetaModel.java.PrimitiveConfig;
import metaModel.minispec.Attribute;
import metaModel.minispec.types.CollectionType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

import java.util.List;

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

        List<PrimitiveConfig> primitiveConfigList = ((JavaMetaModelConfiguration) ((CodeGenVisitor) visitor).getMetaModelConfiguration()).getPrimitiveConfigs();
        List<ModelConfig> modelConfigList = ((JavaMetaModelConfiguration) ((CodeGenVisitor) visitor).getMetaModelConfiguration()).getModelConfigs();

        List<String> candidates = new java.util.ArrayList<>();
        candidates.add(type);
        if (type != null && type.contains("<") && type.contains(">")) {
            String inner = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
            for (String t : inner.split(",")) {
                candidates.add(t.trim());
            }
        }

        for (PrimitiveConfig primitiveConfig : primitiveConfigList) {
            String configName = primitiveConfig.getName();
            String pkg = primitiveConfig.getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                for (String candidate : candidates) {
                    if (candidate.contains(configName)) {
                        String importStmt = "import " + pkg + "." + configName + ";" + System.lineSeparator();
                        if (ctx.imports.indexOf(importStmt) == -1) {
                            ctx.imports.append(importStmt);
                        }
                        break;
                    }
                }
            }
        }

        for (ModelConfig modelConfig : modelConfigList) {
            String configName = modelConfig.getName();
            String pkg = modelConfig.getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                for (String candidate : candidates) {
                    if (candidate.contains(configName)) {
                        String importStmt = "import " + pkg + "." + configName + ";" + System.lineSeparator();
                        if (ctx.imports.indexOf(importStmt) == -1) {
                            ctx.imports.append(importStmt);
                        }
                        break;
                    }
                }
            }
        }

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
