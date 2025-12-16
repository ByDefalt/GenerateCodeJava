package visitor.imports;

import metaModel.*;
import metaModel.types.*;
import visitor.Visitor;

import java.util.Set;
import java.util.TreeSet;

/**
 * JavaVisitor amélioré avec gestion automatique des imports.
 * Respecte le principe OCP : les nouveaux types d'imports sont gérés
 * via l'ajout de nouveaux ImportCollector.
 */
public class JavaVisitorWithImports extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";
    private String currentJavaType = "";
    
    private final ImportCollectorRegistry importRegistry;
    private final Set<String> currentEntityImports;
    
    public JavaVisitorWithImports() {
        this.importRegistry = new ImportCollectorRegistry();
        this.currentEntityImports = new TreeSet<>();
    }
    
    /**
     * Permet d'ajouter des collecteurs personnalisés
     */
    public ImportCollectorRegistry getImportRegistry() {
        return importRegistry;
    }

    @Override
    public void visitModel(Model e) {
        if (e.getEntities() != null) {
            for (Entity ent : e.getEntities()) {
                ent.accept(this);
            }
        }
    }

    @Override
    public void visitEntity(Entity e) {
        methodBuffer = "";
        currentEntityImports.clear();
        
        // Collecter les imports nécessaires pour cette entité
        if (e.getAttributes() != null) {
            for (Attribute attr : e.getAttributes()) {
                currentEntityImports.addAll(importRegistry.collectImports(attr));
            }
        }
        
        StringBuilder classBuffer = new StringBuilder();
        StringBuilder attributesBuffer = new StringBuilder();
        
        // Générer les imports
        if (!currentEntityImports.isEmpty()) {
            for (String importStr : currentEntityImports) {
                classBuffer.append("import ").append(importStr).append(";\n");
            }
            classBuffer.append("\n");
        }
        
        // Générer la classe
        classBuffer.append("public class ").append(e.getName());
        if (e.getSuperEntity() != null) {
            classBuffer.append(" extends ").append(e.getSuperEntity().getName());
        }
        classBuffer.append(" {\n");

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                String savedResult = resultBuffer;
                resultBuffer = "";

                a.accept(this);

                attributesBuffer.append(resultBuffer);
                resultBuffer = savedResult;
            }
        }

        classBuffer.append(attributesBuffer);
        classBuffer.append(methodBuffer);
        classBuffer.append("}\n\n");

        String existingContent = this.resultBuffer;
        this.resultBuffer = existingContent + classBuffer.toString();
    }

    @Override
    public void visitAttribute(Attribute e) {
        currentJavaType = "";
        e.getType().accept(this);
        String javaFullType = currentJavaType;

        resultBuffer += "    private " + javaFullType + " " + e.getName() + ";\n";

        String capitalizedName = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);

        methodBuffer += "\n    public " + javaFullType + " get" + capitalizedName + "() {\n";
        methodBuffer += "        return " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        methodBuffer += "\n    public void set" + capitalizedName + "(" + javaFullType + " " + e.getName() + ") {\n";
        methodBuffer += "        this." + e.getName() + " = " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        if (e.getType() instanceof CollectionType) {
            generateCollectionMethods(e, (CollectionType) e.getType(), capitalizedName);
        }
    }

    @Override
    public void visitSimpleType(SimpleType e) {
        currentJavaType = e.getTypeName();
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        currentJavaType = e.getReferencedEntity().getName();
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        currentJavaType = e.getEntityName();
    }

    @Override
    public void visitArrayType(ArrayType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = elementTypeStr + "[]";
    }

    @Override
    public void visitListType(ListType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "List<" + elementTypeStr + ">";
    }

    @Override
    public void visitSetType(SetType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "Set<" + elementTypeStr + ">";
    }

    @Override
    public void visitBagType(BagType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "List<" + elementTypeStr + ">";
    }

    private void generateCollectionMethods(Attribute attr, CollectionType type, String capitalizedName) {
        type.getElementType().accept(this);
        String baseType = currentJavaType;

        String attrName = attr.getName();

        if (type instanceof ArrayType) {
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else {
            String singularName = capitalizedName.endsWith("s") ?
                    capitalizedName.substring(0, capitalizedName.length() - 1) :
                    capitalizedName;

            String implType;
            if (type instanceof SetType) {
                implType = "HashSet";
            } else {
                implType = "ArrayList";
            }

            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            methodBuffer += "            this." + attrName + " = new " + implType + "<>();\n";
            methodBuffer += "        }\n";

            if (type.getMaxCardinality() != null) {
                methodBuffer += "        if (this." + attrName + ".size() >= " + type.getMaxCardinality() + ") {\n";
                methodBuffer += "            throw new IllegalStateException(\"Cannot add more than " + type.getMaxCardinality() + " items\");\n";
                methodBuffer += "        }\n";
            }

            methodBuffer += "        this." + attrName + ".add(item);\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void remove" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " != null) {\n";

            if (type.getMinCardinality() != null) {
                methodBuffer += "            if (this." + attrName + ".size() <= " + type.getMinCardinality() + ") {\n";
                methodBuffer += "                throw new IllegalStateException(\"Cannot have less than " + type.getMinCardinality() + " items\");\n";
                methodBuffer += "            }\n";
            }

            methodBuffer += "            this." + attrName + ".remove(item);\n";
            methodBuffer += "        }\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public int get" + capitalizedName + "Size() {\n";
            methodBuffer += "        return this." + attrName + " != null ? this." + attrName + ".size() : 0;\n";
            methodBuffer += "    }\n";
        }
    }

    public String getResult() {
        return resultBuffer;
    }
}
